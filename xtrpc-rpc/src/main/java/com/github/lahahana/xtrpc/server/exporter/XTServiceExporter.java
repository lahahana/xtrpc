package com.github.lahahana.xtrpc.server.exporter;

import com.github.lahahana.xtrpc.common.config.api.Application;
import com.github.lahahana.xtrpc.common.config.api.Protocol;
import com.github.lahahana.xtrpc.common.config.api.ServiceConfig;
import com.github.lahahana.xtrpc.common.exception.StubInitializeException;
import com.github.lahahana.xtrpc.server.stub.NettyServiceStub;

import java.util.ArrayList;
import java.util.List;

public class XTServiceExporter {

    private Application application;
    private Protocol protocol;
    private List<ServiceConfig> serviceConfigs;

    private XTServiceExporter(Builder builder) {
        this.application = builder.application;
        this.protocol = builder.protocol;
        this.serviceConfigs = builder.serviceConfigs;
    }

    public void doExport() {
        serviceConfigs.parallelStream().forEach((service) -> {
            NettyServiceStub stub = new NettyServiceStub(service);
            try {
                stub.bootstrap();
            } catch (StubInitializeException e) {
                e.printStackTrace();
            }
        });
    }

    public static Builder builder(){
        return new Builder();
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

        public Builder addServiceConfig(Object service) {
            ServiceConfig serviceConfig = new ServiceConfig();
            serviceConfig.setApplication(application);
            serviceConfig.setProtocol(protocol);
            serviceConfig.setRef(service);
            this.serviceConfigs.add(serviceConfig);
            return this;
        }

        public XTServiceExporter build() {
            return new XTServiceExporter(this);
        }

    }

}