package com.github.lahahana.xtrpc.client.skeleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public abstract class AbstractInvokerHolder implements InvokerHolder {

    private static final Logger logger = LoggerFactory.getLogger(AbstractInvokerHolder.class);

    private Map<String, List<Invoker>> invokersMap = new HashMap<>();//<interface, List<Invoker>>

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private ReentrantReadWriteLock.ReadLock readLock = lock.readLock();

    private ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    private ServiceHolder serviceHolder = ServiceHolder.getInstance();

    /**
     * Mark invoker as Available and remove it from {@link ServiceHolder}
     */
    @Override
    public Invoker hold(Invoker invoker) {
        try {
            writeLock.lock();
            logger.debug("hold invoker:{}", invoker);
            invoker.markAsAvailable();
            final String interfaceName = invoker.getInterface();
            List<Invoker> invokersOfInterface = invokersMap.get(interfaceName);
            if (invokersOfInterface == null) {
                invokersOfInterface = new ArrayList();
                invokersMap.put(interfaceName, invokersOfInterface);
            }
            invokersOfInterface.add(invoker);
            return invoker;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void holdAll(List<Invoker> invokers) {
        invokers.stream().forEach((invoker -> {
            try {
                writeLock.lock();
                logger.debug("hold invoker:{}", invoker);
                invoker.markAsAvailable();
                final String interfaceName = invoker.getInterface();
                List<Invoker> invokersOfInterface = invokersMap.get(interfaceName);
                if (invokersOfInterface == null) {
                    invokersOfInterface = new ArrayList();
                    invokersMap.put(interfaceName, invokersOfInterface);
                }
                invokersOfInterface.add(invoker);
            } finally {
                writeLock.unlock();
            }
        }));

    }

    /**
     * Mark invoker as unavailable,and mark corresponding {@link com.github.lahahana.xtrpc.common.domain.Service} as unavailable too.
     */
    @Override
    public void unhold(Invoker invoker) {
        try {
            writeLock.lock();
            logger.warn("unhold invoker:{}", invoker);
            invoker.markAsUnavailable();
            final String interfaceName = invoker.getInterface();
            List<Invoker> invokersOfInterface = invokersMap.get(interfaceName);
            if (invokersOfInterface != null) {
                invokersOfInterface.remove(invoker);
            }
            serviceHolder.markServiceAsUnavailable(interfaceName, invoker.getAddress());
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public List<Invoker> listAll() {
        try {
            readLock.lock();
            return invokersMap.values().stream().flatMap((x) -> x.stream()).collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public List<Invoker> listInvokersOfInterface(String interfaceName) {
        try {
            readLock.lock();
            return invokersMap.get(interfaceName);
        } finally {
            readLock.unlock();
        }
    }


    @Override
    public void destroy() {
        logger.info("start destroy lifecycle");
        logger.info("end destroy lifecycle");
    }
}
