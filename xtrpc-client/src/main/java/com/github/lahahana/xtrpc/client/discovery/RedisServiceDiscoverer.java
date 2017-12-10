package com.github.lahahana.xtrpc.client.discovery;

import com.github.lahahana.xtrpc.common.config.api.Reference;
import com.github.lahahana.xtrpc.common.config.api.Registry;
import com.github.lahahana.xtrpc.common.config.api.ServiceConfig;
import com.github.lahahana.xtrpc.common.constant.Constraints;
import com.github.lahahana.xtrpc.common.domain.Service;
import com.github.lahahana.xtrpc.common.exception.ServiceNotFoundException;
import com.github.lahahana.xtrpc.common.registry.RedisServiceRegistry;
import com.github.lahahana.xtrpc.common.util.NetworkUtil;
import com.github.lahahana.xtrpc.common.util.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

public class RedisServiceDiscoverer implements ServiceDiscoverer {

    private static final Logger logger = LoggerFactory.getLogger(RedisServiceDiscoverer.class);

    private Registry registry;

    Jedis jedis;

    public RedisServiceDiscoverer(Registry registry) {
        this.registry = registry;
        start();
    }

    public void start() {
        jedis = new Jedis(registry.getHost(), registry.getPort());
    }

    public void close(){
        if(jedis.isConnected()) {
            jedis.close();
        }
    }

    @Override
    public List<Service> discoverService(Reference reference) throws ServiceNotFoundException {
        List<String> serviceListInRedis = jedis.srandmember(reference.getInterfaceName(), 10);
        List<Service> services = new ArrayList<>();
        serviceListInRedis.stream()
                .forEach((str) -> {
                    String[] items = str.split(Constraints.DELIMITER_REDIS);
                    String interfaceName0 = items[0];
                    String protocol0 = items[1];
                    String host = items[2];
                    Integer port = Integer.parseInt(items[3]);
                    boolean available = Boolean.parseBoolean(items[4]);
                    services.add(new Service(interfaceName0, protocol0, host, port, available));
                });
        if(services.size() == 0) {
            throw new ServiceNotFoundException();
        }
        logger.info("discover service of reference={}, size={}",reference,services.size());
        return services;
    }

}
