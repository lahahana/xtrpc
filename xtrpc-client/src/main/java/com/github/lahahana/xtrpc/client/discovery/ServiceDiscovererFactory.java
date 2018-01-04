package com.github.lahahana.xtrpc.client.discovery;

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
                ServiceDiscoverer serviceDiscoverer1 = serviceDiscovererHolder.hold(redisServiceDiscoverer);
                return serviceDiscoverer1 == null ? redisServiceDiscoverer : serviceDiscoverer1;
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
