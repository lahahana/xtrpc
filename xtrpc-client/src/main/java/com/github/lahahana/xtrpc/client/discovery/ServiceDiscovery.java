package com.github.lahahana.xtrpc.client.discovery;

import com.github.lahahana.xtrpc.common.config.api.ServiceConfig;
import com.github.lahahana.xtrpc.common.domain.Service;
import com.github.lahahana.xtrpc.common.exception.ServiceNotFoundException;
import com.github.lahahana.xtrpc.common.util.Mock;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceDiscovery {

    private Map<String, List<Service>> serviceMap = new ConcurrentHashMap<>();

    public ServiceDiscovery() {
    }

    public void discoverServiceByInterface(ServiceConfig serviceConfig) {

    }

    @Mock
    public List<Service> listServicesByInterface(String interfaceClazz) throws ServiceNotFoundException {
        List<Service> services = serviceMap.get(interfaceClazz);
        if (services == null || services.size() == 0)
            throw new ServiceNotFoundException("service not found for interface:" + interfaceClazz);
        return services;
    }
}
