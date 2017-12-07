package com.github.lahahana.xtrpc.client.stub;

import com.github.lahahana.xtrpc.client.ServiceDiscovery;
import com.github.lahahana.xtrpc.client.ServiceInvoker;
import com.github.lahahana.xtrpc.client.dispatch.XTResponseDispatcher;
import com.github.lahahana.xtrpc.client.lb.LoadBalancer;
import com.github.lahahana.xtrpc.client.lb.RandomLoadBalancer;
import com.github.lahahana.xtrpc.common.domain.Service;
import com.github.lahahana.xtrpc.common.domain.XTRequest;
import com.github.lahahana.xtrpc.common.domain.XTResponse;
import com.github.lahahana.xtrpc.common.domain.XTResponseAware;
import com.github.lahahana.xtrpc.common.exception.InvokeTimeoutException;
import com.github.lahahana.xtrpc.common.exception.RejectInvokeException;
import com.github.lahahana.xtrpc.common.exception.ServiceNotAvailableException;
import com.github.lahahana.xtrpc.common.exception.ServiceNotFoundException;
import com.github.lahahana.xtrpc.common.util.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
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

    private ServiceHolder serviceHolder = new ServiceHolder();

    public void initRefService(Service service) {
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
                logger.debug("Connection already established by others, service={} , skip event loop group bootstrap", service);
                return;
            }
            initRefService0(service);
            serviceHolder.holdService(service);
            lockStateTuple0.setV(true);
            logger.debug("connection established: service={}", service);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
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
        //register response aware and hang rpc-call
        XTResponseAware responseAware = new XTResponseAware(xtRequest.getRequestId());
        Object lock = responseDispatcher.register(xtRequest, responseAware);

        //TO-DO add LoadBalance solution support, be sure the service connection is still live
        List<Service> services = serviceHolder.listAliveServices(xtRequest.getInterfaceName());
        Service service = selectService(services);

        try {
            sendXTRequest(service, xtRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Object result = null;
        try {
            synchronized (lock) {
                logger.debug("wait for result of XTRequest:{}", xtRequest);
                lock.wait(xtRequest.getTimeout());
                XTResponse xtResponse = responseAware.getResponse();
                if (xtResponse != null) {
                    result = xtResponse.getResult();
                } else {
                    //fail-over retry
                    logger.error("{}", xtResponse);
                    throw new InvokeTimeoutException("No response received from server side");
                }
            }
        } catch (InterruptedException e) {
            throw new InvokeTimeoutException(e);
        }

        return result;
    }

    protected List<Service> discoverService(String interfaceName) throws ServiceNotFoundException {
        return serviceDiscovery.listServicesByInterface(interfaceName);
    }

    protected Service selectService(List<Service> services){
        return loadBalancer.selectService(services);
    }

    protected abstract void initRefService0(Service service) throws ServiceNotAvailableException;

    protected abstract void sendXTRequest(Service service, XTRequest xtRequest) throws Exception;

    protected abstract void shutdownRefService(Service service);

    protected abstract void shutdown();

    protected XTRequest buildXTRequest(Method method, Object[] args) {
        Class<?> clazz = method.getDeclaringClass();
        String interfaceName = clazz.getName();
        String methodName = method.getName();
        Class<?>[] argsType = method.getParameterTypes();

        long requestId = requestIdCounter.getAndIncrement();
        XTRequest xtRequest = new XTRequest(requestId);
        xtRequest.setInterfaceName(clazz.getName());
        xtRequest.setMethod(methodName);
        xtRequest.setArgsType(argsType);
        xtRequest.setTimeout(2000l);
        xtRequest.setArgs(args);
        return xtRequest;
    }

    private static class ServiceHolder {

        private Map<String, List<Service>> serviceMap = new ConcurrentHashMap<>();

        public void holdService(Service service) {
            List<Service> servicesOfInterface = serviceMap.get(service.getServiceInterface());
            if(servicesOfInterface == null) {
                servicesOfInterface = new ArrayList<>();
            }
            servicesOfInterface.add(service);
            serviceMap.put(service.getServiceInterface(), servicesOfInterface);
        }

        public List<Service> listAliveServices(String interfaceName){
            return serviceMap.get(interfaceName);
        }
    }
}
