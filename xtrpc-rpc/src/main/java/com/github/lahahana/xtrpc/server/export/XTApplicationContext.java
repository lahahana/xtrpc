package com.github.lahahana.xtrpc.server.export;

import com.github.lahahana.xtrpc.common.config.api.Application;
import com.github.lahahana.xtrpc.common.config.api.Protocol;
import com.github.lahahana.xtrpc.common.config.api.ServiceConfig;
import com.github.lahahana.xtrpc.common.exception.StubInitializeException;
import com.github.lahahana.xtrpc.server.stub.NettyServiceStub;

import java.util.ArrayList;
import java.util.List;

public class XTApplicationContext {

    private Application application;
    private Protocol protocol;
    private List<ServiceConfig> serviceConfigs;

    private XTApplicationContext(Builder builder) {
        this.application = builder.application;
        this.protocol = builder.protocol;
        this.serviceConfigs = builder.serviceConfigs;
    }

    public void start() {
        serviceConfigs.parallelStream().forEach((service) -> {
            NettyServiceStub stub = new NettyServiceStub(service);
            try {
                stub.bootstrap();
            } catch (StubInitializeException e) {
                e.printStackTrace();
            }
        });
    }

    public static class Builder {

        private Application application;
        private Protocol protocol;
        private List<ServiceConfig> serviceConfigs = new ArrayList<>();

        public Builder() {
        }

        public Builder setApplication(Application application) {
            this.application = application;
            return this;
        }

        public Builder setProtocol(Protocol protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder addService(Object service) {
            ServiceConfig serviceConfig = new ServiceConfig();
            serviceConfig.setApplication(application);
            serviceConfig.setProtocol(protocol);
            serviceConfig.setRef(service);
            this.serviceConfigs.add(serviceConfig);
            return this;
        }

        public Builder addService(ServiceConfig serviceConfig) {
            this.serviceConfigs.add(serviceConfig);
            return this;
        }

        public XTApplicationContext build() {
            return new XTApplicationContext(this);
        }

    }

}
