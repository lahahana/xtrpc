package com.github.lahahana.xtrpc.client.importer;

import com.github.lahahana.xtrpc.common.config.api.Registry;
import lombok.Getter;

@Getter
public class RegistryRefService implements RefService {

    private Class<?> serviceInterface;

    private Registry registry;

    public RegistryRefService(Class<?> serviceInterface, Registry registry) {
        this.serviceInterface = serviceInterface;
        this.registry = registry;
    }

    @Override
    public Class<?> getInterface() {
        return serviceInterface;
    }

    /**
     * @return host of registry
     */
    public String getHost() {
        return registry.getHost();
    }

    /**
     * @return port of registry
     */
    public int getPort() {
        return registry.getPort();
    }
}
