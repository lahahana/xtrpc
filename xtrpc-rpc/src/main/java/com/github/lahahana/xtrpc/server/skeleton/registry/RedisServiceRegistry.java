package com.github.lahahana.xtrpc.server.skeleton.registry;


import com.github.lahahana.xtrpc.common.config.api.Registry;
import com.github.lahahana.xtrpc.common.constant.Constraints;
import com.github.lahahana.xtrpc.common.domain.Service;
import com.github.lahahana.xtrpc.common.exception.ServiceRegisterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

public class RedisServiceRegistry implements ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(RedisServiceRegistry.class);

    private Registry registry;

    private Jedis jedis;

    public RedisServiceRegistry(Registry registry) {
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
    public void register(Service service) throws ServiceRegisterException {
        StringBuilder redisRegisterInfoBuilder = new StringBuilder();
        redisRegisterInfoBuilder.append(service.getServiceInterface());
        redisRegisterInfoBuilder.append(Constraints.DELIMITER_REDIS);
        redisRegisterInfoBuilder.append(service.getProtocol());
        redisRegisterInfoBuilder.append(Constraints.DELIMITER_REDIS);
        redisRegisterInfoBuilder.append(service.getHost());
        redisRegisterInfoBuilder.append(Constraints.DELIMITER_REDIS);
        redisRegisterInfoBuilder.append(service.getPort());
        redisRegisterInfoBuilder.append(Constraints.DELIMITER_REDIS);
        redisRegisterInfoBuilder.append(service.isAvailable());
        String registryInfo = redisRegisterInfoBuilder.toString();
        boolean opStatus = false;
        try {
            opStatus = jedis.sadd(service.getServiceInterface(),registryInfo) == 1 ? true : false;
            if(opStatus) {
                logger.info("service={} register to registry={}",registryInfo, registry);
            }else {
                logger.info("service={} already register to registry={}",registryInfo, registry);
            }
        } catch (Exception e) {
            throw new ServiceRegisterException(e);
        }
    }

    @Override
    public boolean unregister(Service service)  {
        StringBuilder redisRegisterInfoBuilder = new StringBuilder();
        redisRegisterInfoBuilder.append(service.getServiceInterface());
        redisRegisterInfoBuilder.append(Constraints.DELIMITER_REDIS);
        redisRegisterInfoBuilder.append(service.getProtocol());
        redisRegisterInfoBuilder.append(Constraints.DELIMITER_REDIS);
        redisRegisterInfoBuilder.append(service.getHost());
        redisRegisterInfoBuilder.append(Constraints.DELIMITER_REDIS);
        redisRegisterInfoBuilder.append(service.getPort());
        redisRegisterInfoBuilder.append(Constraints.DELIMITER_REDIS);
        redisRegisterInfoBuilder.append(service.isAvailable());
        String registryInfo = redisRegisterInfoBuilder.toString();
        boolean opStatus = false;
        try {
            opStatus = jedis.srem(service.getServiceInterface(),registryInfo) == 1 ? true : false;
            if(opStatus) {
                logger.info("unregister service={}",service);
            }else {
                logger.info("service={} already unRegister",service);
            }
        } catch (Exception e) {
            logger.error("Fail to unregister:{} ", service, e);
        }
        return opStatus;
    }
}
