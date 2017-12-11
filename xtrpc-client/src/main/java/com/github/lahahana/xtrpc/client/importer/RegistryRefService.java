package com.github.lahahana.xtrpc.client.importer;

import com.github.lahahana.xtrpc.common.config.api.Protocol;
import com.github.lahahana.xtrpc.common.config.api.Registry;
import lombok.Getter;

@Getter
public class RegistryRefService implements RefService {

    private Class<?> serviceInterface;

    private Protocol protocol;

    private Registry registry;

    public RegistryRefService(Class<?> serviceInterface, Protocol protocol, Registry registry) {
        this.serviceInterface = serviceInterface;
        this.protocol = protocol;
        this.registry = registry;
    }

    @Override
    public Class<?> getInterface() {
        return serviceInterface;
    }

    /**
     * @return host of registry
     * */
    @Override
    public String getHost() {
        return registry.getHost();
    }

    /**
     * @return port of registry
     * */
    @Override
    public int getPort() {
        return registry.getPort();
    }
}
