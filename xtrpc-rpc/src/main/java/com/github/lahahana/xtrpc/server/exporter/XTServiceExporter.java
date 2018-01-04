package com.github.lahahana.xtrpc.server.exporter;

import com.github.lahahana.xtrpc.common.config.XTProtocol;
import com.github.lahahana.xtrpc.common.config.api.Application;
import com.github.lahahana.xtrpc.common.config.api.Protocol;
import com.github.lahahana.xtrpc.common.config.api.Registry;
import com.github.lahahana.xtrpc.common.config.api.ServiceConfig;
import com.github.lahahana.xtrpc.common.exception.ServiceExportException;
import com.github.lahahana.xtrpc.common.exception.StubInitializeException;
import com.github.lahahana.xtrpc.common.util.AssertsUtil;
import com.github.lahahana.xtrpc.server.stub.NettyServiceStub;

public class XTServiceExporter {

    private ServiceConfig serviceConfig;

    private XTServiceExporter(Builder builder) {
        serviceConfig = new ServiceConfig();
        serviceConfig.setApplication(builder.application);
        serviceConfig.setProtocol(builder.protocol);
        serviceConfig.setInterfaceClass(builder.interfaceClass);
        serviceConfig.setRef(builder.serviceRef);
        serviceConfig.setRegistry(builder.registry);
    }

    public void doExport() throws ServiceExportException {
        NettyServiceStub stub = new NettyServiceStub(serviceConfig);
        try {
            stub.bootstrap();
        } catch (StubInitializeException e) {
            throw new ServiceExportException(e);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Application application;
        private Protocol protocol;
        private Registry registry;
        private Class<?> interfaceClass;
        private Object serviceRef;

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

        /**
         * Service will not register to service registry if {@link Registry} not set
         */
        public Builder setRegistry(Registry registry) {
            this.registry = registry;
            return this;
        }

        /**
         * @param interfaceClass the interface of service which you want to established as remote ref service
         */
        public Builder setService(Class<?> interfaceClass, Object serviceRef) {
            if (!interfaceClass.isAssignableFrom(serviceRef.getClass()))
                throw new IllegalArgumentException("service ref must implement interfaceClass");
            this.interfaceClass = interfaceClass;
            this.serviceRef = serviceRef;
            return this;
        }

        public XTServiceExporter build() {
            AssertsUtil.ensureNotNull("application", application);
            AssertsUtil.ensureNotNull("interfaceClass", interfaceClass);
            AssertsUtil.ensureNotNull("serviceRef", serviceRef);
            if (protocol == null) {
                protocol = new XTProtocol();
            }
            return new XTServiceExporter(this);
        }

    }

}
