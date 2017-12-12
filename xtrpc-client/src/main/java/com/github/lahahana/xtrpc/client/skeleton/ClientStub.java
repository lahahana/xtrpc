package com.github.lahahana.xtrpc.client.skeleton;

import com.github.lahahana.xtrpc.client.discovery.ServiceDiscoverer;
import com.github.lahahana.xtrpc.client.discovery.ServiceDiscovererFactory;
import com.github.lahahana.xtrpc.client.dispatch.XTResponseDispatcher;
import com.github.lahahana.xtrpc.client.importer.RegistryRefService;
import com.github.lahahana.xtrpc.client.lb.LoadBalancer;
import com.github.lahahana.xtrpc.client.lb.RandomLoadBalancer;
import com.github.lahahana.xtrpc.common.config.api.Protocol;
import com.github.lahahana.xtrpc.common.config.api.Reference;
import com.github.lahahana.xtrpc.common.constant.Constraints;
import com.github.lahahana.xtrpc.common.domain.Service;
import com.github.lahahana.xtrpc.common.domain.XTRequest;
import com.github.lahahana.xtrpc.common.domain.XTResponse;
import com.github.lahahana.xtrpc.common.domain.XTResponseAware;
import com.github.lahahana.xtrpc.common.exception.*;
import com.github.lahahana.xtrpc.common.stub.XTStub;
import com.github.lahahana.xtrpc.common.util.NetworkUtil;
import com.github.lahahana.xtrpc.common.util.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class ClientStub implements XTStub {

    private static final Logger logger = LoggerFactory.getLogger(ClientStub.class);

    protected volatile boolean online = true;

    private final AtomicLong requestIdCounter = new AtomicLong();

    private XTResponseDispatcher responseDispatcher = XTResponseDispatcher.getInstance();

    private Map<String, Tuple<Lock, Boolean>> initializationLockMap = new ConcurrentHashMap<>();//true means connection established

    private ServiceDiscovererFactory serviceDiscovererFactory = ServiceDiscovererFactory.getInstance();

    private LoadBalancer loadBalancer = new RandomLoadBalancer();

    private final ServiceHolder serviceHolder = ServiceHolder.getInstance();

    private final InvokerHolderFactory invokerHolderFactory = InvokerHolderFactory.getInstance();

    private ScheduledHeartBeatInvoker scheduledHeartBeatInvoker;


    public ClientStub(ScheduledHeartBeatInvoker scheduledHeartBeatInvoker) {
        this.scheduledHeartBeatInvoker = scheduledHeartBeatInvoker;
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                ClientStub.this.destroy();
            }
        });
    }

    @Override
    public void bootstrap() {
        scheduledHeartBeatInvoker.start();
    }

    @Override
    public void destroy() {
        //destroy service level
        logger.debug("start destroy lifecycle");
        serviceDiscovererFactory.destroy();
        serviceHolder.destroy();
        //destroy invoker level
        scheduledHeartBeatInvoker.destroy();
        invokerHolderFactory.destroy();
        logger.debug("end destroy lifecycle");
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
            //hint: always hold service even if service is not available currently
            serviceHolder.hold(service);
            initRefService0(service);
            logger.debug("connection established: refService={}", service);
            lockStateTuple0.setV(true);
        } catch (Exception e) {
            service.setAvailable(false);
            serviceHolder.markServiceAsUnavailable(service.getServiceInterface(), NetworkUtil.assembleAddress(service.getHost(),service.getPort()));
            throw new ServiceNotAvailableException(e);
        } finally {
            lock.unlock();
        }
    }

    public void initRegistryRefService(RegistryRefService registryRefService) throws ServiceNotFoundException, NoAvailableServicesException {
        logger.debug("start to establish connection: registryRefService={}", registryRefService);
        Reference reference = new Reference();
        reference.setInterfaceName(registryRefService.getServiceInterface().getName());
        reference.setRegistry(registryRefService.getRegistry());
        ServiceDiscoverer serviceDiscoverer = serviceDiscovererFactory.getServiceDiscoverer(reference.getRegistry());
        List<Service> services = serviceDiscoverer.discoverService(reference);

        AtomicInteger unavailableServiceCounter = new AtomicInteger();
        services.parallelStream().forEach((service) -> {
            try {
                initRefService(service);
            } catch (ServiceNotAvailableException e) {
                logger.warn("service not available, refService={}", service);
                //hint: partial ref service unavailable tolerance
                int unavailableServiceCount = unavailableServiceCounter.incrementAndGet();
                if(unavailableServiceCount == services.size()) {
                    throw new NoAvailableServicesException();
                }
            }
        });
    }

    public Object invoke(Method method, Object[] args, Protocol protocol) throws Exception {

        if (!online) {
            throw new RejectInvokeException("client stub offline");
        }
        XTRequest xtRequest = buildXTRequest(method, args);
        InvokerHolder invokerHolder = invokerHolderFactory.getInvokerHolder(protocol);
        List<Invoker> invokers = invokerHolder.listInvokersOfInterface(xtRequest.getInterfaceName());
        //TO-DO add LoadBalance solution support, be sure the service connection is still alive
        Invoker invoker = loadBalancer.selectInvoker(invokers);

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
                Class<?>[] exceptionTypes = method.getExceptionTypes();
                if(exceptionTypes == null || exceptionTypes.length == 0) {
                    //it is a runtime exception
                    RuntimeException exception = new RuntimeException(xtResponse.getThrowable());
                    throw exception;
                }else {
                    //is a predefined exception
                }
            } else if (code == Constraints.STATUS_ERROR) {
                logger.debug("Fail to invoke request by invoker={}", invoker);
                invokerHolder.unhold(invoker);
                result = failOverRetry(invokerHolder, invokers, xtRequest, method);
            }
        } catch (XTRequestInvokeException | TimeoutException e) {
            logger.debug("Fail to invoke request by invoker={}", invoker, e);
            result = failOverRetry(invokerHolder, invokers, xtRequest, method);

        } catch (InstantiationException | IllegalAccessException e) {

        }
        return result;
    }

    private Object failOverRetry(InvokerHolder invokerHolder, List<Invoker> invokers, XTRequest xtRequest, Method method) throws NoAvailableServicesException, Exception {
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
                        invokerHolder.unhold(invoker0);
                        continue;
                    }
                }
            } catch (XTRequestInvokeException | TimeoutException e1) {
                logger.debug("Fail to invoke request by invoker={}", invoker0, e1);
                if (i == invokers.size() - 1) {
                    //TO-DO
                    logger.debug("No more invoker to fail, reload service from service registry", invoker0, e1);
                    throw new NoAvailableServicesException();
                }
            }
        }
        return null;
    }

    /**
     * Sub class need to implement this method to fulfill remote service initiation logic
     * */
    protected abstract void initRefService0(Service service) throws ServiceNotAvailableException;

    public abstract void shutdownRefService(Service service);

    public abstract void shutdown();

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
