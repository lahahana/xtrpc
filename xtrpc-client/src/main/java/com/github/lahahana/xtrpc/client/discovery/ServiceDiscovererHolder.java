package com.github.lahahana.xtrpc.client.discovery;

import com.github.lahahana.xtrpc.common.base.Holder;
import com.github.lahahana.xtrpc.common.config.api.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ServiceDiscovererHolder implements Holder<ServiceDiscoverer> {

    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovererHolder.class);

    private Map<String, ServiceDiscoverer> discoverersMap = new ConcurrentHashMap<>();//<unique string val of Registry, List<ServiceDiscoverer>>


    @Override
    public ServiceDiscoverer hold(ServiceDiscoverer serviceDiscoverer) {
        Registry registry = serviceDiscoverer.getRegistry();
        String uniqueKey = registry.getUniqueKey();
        return discoverersMap.putIfAbsent(uniqueKey, serviceDiscoverer);
    }

    @Override
    public void unhold(ServiceDiscoverer serviceDiscoverer) {
        Registry registry = serviceDiscoverer.getRegistry();
        String uniqueKey = registry.getUniqueKey();
        discoverersMap.remove(uniqueKey);
    }

    public ServiceDiscoverer get(Registry registry) {
        String uniqueKey = registry.getUniqueKey();
        return discoverersMap.get(uniqueKey);
    }

    @Override
    public void holdAll(List<ServiceDiscoverer> l) {

    }

    @Override
    public List<ServiceDiscoverer> listAll() {
        return null;
    }

    @Override
    public void destroy() {
        discoverersMap.values().parallelStream().forEach((discoverer) -> discoverer.destroy());
    }
}
