package com.github.lahahana.xtrpc.client.skeleton.discovery;

import com.github.lahahana.xtrpc.common.base.SingletonDestroyableFactory;
import com.github.lahahana.xtrpc.common.config.api.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServiceDiscovererFactory extends SingletonDestroyableFactory {

    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovererFactory.class);

    private static volatile ServiceDiscovererFactory instance;

    private ServiceDiscovererHolder serviceDiscovererHolder = new ServiceDiscovererHolder();

    private ServiceDiscovererFactory() {
    }

    public static ServiceDiscovererFactory getInstance() {
        if (instance == null) {
            synchronized (ServiceDiscovererFactory.class) {
                if (instance == null) {
                    instance = new ServiceDiscovererFactory();
                }
            }
        }
        return instance;
    }

    public ServiceDiscoverer getServiceDiscoverer(Registry registry) {
        ServiceDiscoverer serviceDiscoverer = serviceDiscovererHolder.get(registry);
        if (serviceDiscoverer != null) {
            return serviceDiscoverer;
        }

        String type = registry.getType();
        switch (type) {
            case "redis":
                ServiceDiscoverer redisServiceDiscoverer = new RedisServiceDiscoverer(registry);
                ServiceDiscoverer redisServiceDiscoverer2 = serviceDiscovererHolder.hold(redisServiceDiscoverer);
                return redisServiceDiscoverer2 == null ? redisServiceDiscoverer : redisServiceDiscoverer2;
            default:
                throw new IllegalArgumentException("Unknown registry type" + type);
        }
    }

    @Override
    public void destroy() {
        logger.debug("start destroy lifecycle");
        serviceDiscovererHolder.destroy();
        logger.debug("end destroy lifecycle");
    }
}
