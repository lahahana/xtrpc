package com.github.lahahana.xtrpc.client;

import com.github.lahahana.xtrpc.common.domain.Service;
import com.github.lahahana.xtrpc.common.exception.ServiceNotFoundException;
import com.github.lahahana.xtrpc.common.util.Mock;
import com.github.lahahana.xtrpc.common.util.NetworkUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceDiscovery {

    private Map<String, List<Service>> serviceMap = new ConcurrentHashMap<>();

    public ServiceDiscovery() {
        Service service = new Service("com.github.lahahana.xtrpc.test.service.AddressService", NetworkUtil.getLocalHostInetAddress().getHostAddress(), 8088);
        List<Service> services = new ArrayList<>();
        services.add(service);
        serviceMap.put(service.getInterfaceClazz(), services);
    }

    @Mock
    public List<Service> listServicesByInterface(String interfaceClazz) throws ServiceNotFoundException {
        List<Service> services = serviceMap.get(interfaceClazz);
        if (services == null || services.size() == 0)
            throw new ServiceNotFoundException("service not found for interface:" + interfaceClazz);
        return services;
    }
}
