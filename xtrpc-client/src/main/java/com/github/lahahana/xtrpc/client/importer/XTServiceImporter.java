package com.github.lahahana.xtrpc.client.importer;

import com.github.lahahana.xtrpc.client.proxy.RPCClientProxy;
import com.github.lahahana.xtrpc.client.netty.NettyClientStub;
import com.github.lahahana.xtrpc.common.config.api.Application;
import com.github.lahahana.xtrpc.common.config.api.Protocol;
import com.github.lahahana.xtrpc.common.config.api.Registry;
import com.github.lahahana.xtrpc.common.domain.Service;
import com.github.lahahana.xtrpc.common.exception.ServiceNotAvailableException;
import com.github.lahahana.xtrpc.common.exception.ServiceNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class XTServiceImporter {

    private Application application;
    private Registry registry;
    private List<DirectRefService> directRefServices;
    private List<RegistryRefService> registryRefServices = new ArrayList<>();

    private XTServiceImporter(XTServiceImporter.Builder builder) {
        this.application = builder.application;
        this.directRefServices = builder.directRefServices;
        this.registryRefServices = builder.registryRefServices;
    }

    public void doImport() {
        NettyClientStub stub = NettyClientStub.getInstance();
        directRefServices.stream().forEach((s) -> {
            Service service = new Service(s.getServiceInterface().getName(), s.getProtocol().getName(), s.getHost(), s.getPort());
            try {
                stub.initRefService(service);
            } catch (ServiceNotAvailableException e) {
                boolean sameDirectRefServiceExsits = directRefServices.stream().filter((s2) -> s2.getInterface().equals(service.getServiceInterface())).count() > 1 ? true : false;
                if (sameDirectRefServiceExsits) {
                    //TO-DO ignore if can accept one ref service offline
                }
            }
        });

        registryRefServices.stream().forEach((s) -> {
            try {
                stub.initRegistryRefService(s);
            } catch (ServiceNotFoundException e) {
                 e.printStackTrace();
            }
        });
    }

    public <T> T getRefService(Class<T> serviceInterface) {
        return RPCClientProxy.getProxy(serviceInterface);
    }

    public static class Builder {

        private Application application;
        private List<DirectRefService> directRefServices = new ArrayList<>();
        private List<RegistryRefService> registryRefServices = new ArrayList<>();

        public Builder() {
        }

        public Builder setApplication(Application application) {
            this.application = application;
            return this;
        }

        public Builder addRegistryRefService(RegistryRefService service) {
            if (!service.getServiceInterface().isInterface()) {
                throw new IllegalArgumentException("parameter must be interface");
            }
            registryRefServices.add(service);
            return this;
        }

        /**
         * Used for direct ref service , skip service discovery through registry
         */
        public Builder addDirectRefService(DirectRefService service) {
            if (!service.getServiceInterface().isInterface()) {
                throw new IllegalArgumentException("parameter must be interface");
            }
            directRefServices.add(service);
            return this;
        }

        public XTServiceImporter build() {
            return new XTServiceImporter(this);
        }

    }
}
