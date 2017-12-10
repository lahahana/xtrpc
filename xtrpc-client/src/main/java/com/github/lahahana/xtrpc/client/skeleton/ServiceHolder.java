package com.github.lahahana.xtrpc.client.skeleton;

import com.github.lahahana.xtrpc.common.domain.Service;
import com.github.lahahana.xtrpc.common.util.NetworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ServiceHolder {

    private static final Logger logger = LoggerFactory.getLogger(ServiceHolder.class);

    private static volatile ServiceHolder instance;

    private Map<String, List<Service>> serviceMap = new HashMap<>();

    private ServiceHolder(){}

    public static ServiceHolder getInstance() {
        if (instance == null) {
            synchronized (ServiceHolder.class) {
                if (instance == null) {
                    instance = new ServiceHolder();
                }
            }
        }
        return instance;
    }

    public synchronized void holdService(Service service) {
        logger.debug("hold service:{}",service);
        List<Service> servicesOfInterface = serviceMap.get(service.getServiceInterface());
        if (servicesOfInterface == null) {
            servicesOfInterface = new ArrayList<>();
        }
        servicesOfInterface.add(service);
        serviceMap.put(service.getServiceInterface(), servicesOfInterface);
    }

    public synchronized void holdServices(String serviceInterface, List<Service> services) {
        logger.debug("hold services: interface={}, size={}",serviceInterface, services.size());
        List<Service> servicesOfInterface = serviceMap.get(serviceInterface);
        if (servicesOfInterface == null) {
            servicesOfInterface = new ArrayList<>();
        }
        servicesOfInterface.addAll(services);
        serviceMap.put(serviceInterface, servicesOfInterface);
    }

    public synchronized List<Service> listAvailableServices(String interfaceName) {
        return serviceMap.get(interfaceName).stream().filter((service -> service.isAvailable())).collect(Collectors.toList());
    }

    public synchronized void markServiceAsUnavailable(String interfaceName, String address) {
        logger.warn("mark service as unavailable:interface={}, address={}",interfaceName, address);
        serviceMap.get(interfaceName)
                .stream()
                .filter((s) -> NetworkUtil.assembleAddress(s.getHost(), s.getPort()).equals(address))
                .findFirst()
                .ifPresent((service -> service.setAvailable(false)));
    }

    public synchronized void markEndpointAsUnavailable(String address) {
        logger.warn("mark endpoint as unavailable:address={}", address);
        serviceMap.values()
                .stream()
                .flatMap((x) -> x.stream())
                .filter((s) -> NetworkUtil.assembleAddress(s.getHost(), s.getPort()).equals(address))
                .forEach(service -> service.setAvailable(false));
    }
}