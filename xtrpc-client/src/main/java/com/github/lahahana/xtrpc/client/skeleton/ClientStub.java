package com.github.lahahana.xtrpc.client.skeleton;

import com.github.lahahana.xtrpc.client.discovery.ServiceDiscovery;
import com.github.lahahana.xtrpc.client.dispatch.XTResponseDispatcher;
import com.github.lahahana.xtrpc.client.lb.LoadBalancer;
import com.github.lahahana.xtrpc.client.lb.RandomLoadBalancer;
import com.github.lahahana.xtrpc.common.constant.Constraints;
import com.github.lahahana.xtrpc.common.domain.*;
import com.github.lahahana.xtrpc.common.exception.*;
import com.github.lahahana.xtrpc.common.util.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class ClientStub {

    private static final Logger logger = LoggerFactory.getLogger(ClientStub.class);

    protected volatile boolean online = true;

    private AtomicLong requestIdCounter = new AtomicLong();

    private XTResponseDispatcher responseDispatcher = XTResponseDispatcher.getInstance();

    private Map<String, Tuple<Lock, Boolean>> initializationLockMap = new ConcurrentHashMap<>();//true means connection established

    private LoadBalancer loadBalancer = new RandomLoadBalancer();

    private ServiceDiscovery serviceDiscovery = new ServiceDiscovery();

    private ServiceHolder serviceHolder = ServiceHolder.getInstance();

    private InvokerHolder invokerHolder = InvokerHolderFactory.getInvokerHolder();

    private ScheduledHeartBeatInvoker scheduledHeartBeatInvoker;

    public ClientStub(ScheduledHeartBeatInvoker scheduledHeartBeatInvoker) {
        this.scheduledHeartBeatInvoker = scheduledHeartBeatInvoker;
        this.scheduledHeartBeatInvoker.start();
    }

    public void initRefService(Service service) throws ServiceNotAvailableException {
        logger.debug("start to establish connection: refService={}", service);
        Tuple<Lock, Boolean> lockStateTuple = new Tuple<>(new ReentrantLock(), false);
        Tuple<Lock, Boolean> lockStateTuple0 = initializationLockMap.putIfAbsent(service.getUniqueKey(), lockStateTuple);
        if (lockStateTuple0 == null) {
            lockStateTuple0 = lockStateTuple;
        }
        Lock lock = lockStateTuple0.getK();
        try {
            lock.lock();
            if (lockStateTuple0.getV()) {
                logger.debug("Connection already established by others, refService={}.", service);
                return;
            }
            initRefService0(service);
            serviceHolder.holdService(service);
            lockStateTuple0.setV(true);
            logger.debug("connection established: refService={}", service);
        } catch (Exception e) {
            throw new ServiceNotAvailableException(e);
        } finally {
            lock.unlock();
        }
    }

    public Object invoke(Object obj, Method method, Object[] args) throws Exception {

        if (!online) {
            throw new RejectInvokeException("client stub offline");
        }
        XTRequest xtRequest = buildXTRequest(method, args);
        List<Invoker> invokers = invokerHolder.listInvokersOfInterface(xtRequest.getInterfaceName());
        //TO-DO add LoadBalance solution support, be sure the service connection is still alive
        Invoker invoker = selectInvoker(invokers);

        //register response aware and hang rpc-call
        XTResponseAware responseAware = responseDispatcher.register(xtRequest);
        Object result = null;

        try {
            invoker.invokeXTRequest(xtRequest);
            logger.debug("wait for result of XTRequest:{}", xtRequest);
            XTResponse xtResponse = responseAware.aware(xtRequest.getTimeout());
            final int code = xtResponse == null ? Constraints.STATUS_ERROR : xtResponse.getStatusCode();
            if (code == Constraints.STATUS_OK) {
                result = xtResponse.getResult();
            } else if (code == Constraints.STATUS_METHOD_ERROR) {
                Exception exception = (Exception) method.getExceptionTypes()[0].newInstance();
                exception.initCause(new Throwable(xtResponse.getThrowable()));
                throw exception;
            } else if (code == Constraints.STATUS_ERROR) {
                logger.debug("Fail to invoke request by invoker={}", invoker);
                invokerHolder.unholdInvoker(invoker);
                result = failOverRetry(invokers, xtRequest, method);
            }
        } catch (XTRequestInvokeException | TimeoutException e) {
            logger.debug("Fail to invoke request by invoker={}", invoker, e);
            result = failOverRetry(invokers, xtRequest, method);

        } catch (InstantiationException | IllegalAccessException e) {

        }
        return result;
    }

    private Object failOverRetry(List<Invoker> invokers, XTRequest xtRequest, Method method) throws NoAvailableServersException, Exception {
        for (int i = 0; i < invokers.size(); i++) {
            Invoker invoker0 = invokers.get(i);
            logger.info("Fail over retry, times={}, invoker={}", i + 1, invoker0);
            try {
                XTResponseAware responseAware = responseDispatcher.register(xtRequest);
                invoker0.invokeXTRequest(xtRequest);
                logger.debug("wait for result of XTRequest:{}", xtRequest);
                XTResponse xtResponse = responseAware.aware(xtRequest.getTimeout());
                if (xtResponse != null) {
                    final int code = xtResponse.getStatusCode();
                    if (code == Constraints.STATUS_OK) {
                        return xtResponse.getResult();
                    } else if (code == Constraints.STATUS_METHOD_ERROR) {
                        Exception exception = (Exception) method.getExceptionTypes()[0].newInstance();
                        exception.initCause(new Throwable(xtResponse.getThrowable()));
                        throw exception;
                    } else if (code == Constraints.STATUS_ERROR) {
                        invokerHolder.unholdInvoker(invoker0);
                        continue;
                    }
                }
            } catch (XTRequestInvokeException | TimeoutException e1) {
                logger.debug("Fail to invoke request by invoker={}", invoker0, e1);
                if (i == invokers.size() - 1) {
                    //TO-DO
                    logger.debug("No more invoker to fail, reload service from service registry", invoker0, e1);
                    throw new NoAvailableServersException();
                }
            }
        }
        return null;
    }

    protected List<Service> discoverService(String interfaceName) throws ServiceNotFoundException {
        return serviceDiscovery.listServicesByInterface(interfaceName);
    }

    protected Invoker selectInvoker(List<Invoker> invokers) {
        return loadBalancer.selectInvoker(invokers);
    }

    /**
     * Sub class need to implement this method to fulfill remote service initiation logic
     * */
    protected abstract void initRefService0(Service service) throws ServiceNotAvailableException;

    protected abstract void shutdownRefService(Service service);

    protected abstract void shutdown();

    protected XTRequest buildXTRequest(Method method, Object[] args) {
        Class<?> clazz = method.getDeclaringClass();
        String interfaceName = clazz.getName();
        String methodName = method.getName();
        Class<?>[] argsType = method.getParameterTypes();

        long requestId = requestIdCounter.getAndIncrement();
        XTRequest xtRequest = new XTRequest(requestId);
        xtRequest.setInterfaceName(interfaceName);
        xtRequest.setMethod(methodName);
        xtRequest.setArgsType(argsType);
        xtRequest.setTimeout(2000L);
        xtRequest.setArgs(args);
        return xtRequest;
    }

}
