package com.github.lahahana.xtrpc.server.registry;

import com.github.lahahana.xtrpc.common.config.api.Registry;

public class ServiceRegistryFactory {

    public static synchronized ServiceRegistry getServiceRegistry(Registry registry) {
        String type = registry.getType();
        switch (type) {
            case "redis":return new RedisServiceRegistry(registry);
            default: throw new IllegalArgumentException("unknown registry type:" + type);
        }
    }
}
