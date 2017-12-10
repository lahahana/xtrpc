package com.github.lahahana.xtrpc.client.discovery;

import com.github.lahahana.xtrpc.common.config.api.Registry;

public class ServiceDiscovererFactory {

    public static synchronized ServiceDiscoverer getServiceDiscoverer(Registry registry) {
        String type = registry.getType();
        switch (type) {
            case "redis": return new RedisServiceDiscoverer(registry);
            default: throw new IllegalArgumentException("Unknown registry type" + type);
        }
    }
}
