package com.github.lahahana.xtrpc.server.skeleton.stub;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceHolder {

    private volatile static ServiceHolder instance;

    private Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    private ServiceHolder() {
    }

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

    public void holdService(String interfaceName, Object service) {
        serviceMap.put(interfaceName, service);
    }

    public Object getService(String interfaceName){
        return serviceMap.get(interfaceName);
    }
}
