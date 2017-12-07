package com.github.lahahana.xtrpc.client.ref;

import com.github.lahahana.xtrpc.client.stub.NettyClientStub;
import com.github.lahahana.xtrpc.client.proxy.RPCClientProxy;
import com.github.lahahana.xtrpc.common.config.DirectRefService;
import com.github.lahahana.xtrpc.common.config.api.*;
import com.github.lahahana.xtrpc.common.domain.Service;

import java.util.ArrayList;
import java.util.List;

public class XTClientApplicationContext {

    private Application application;
    private Registry registry;
    private String protocol;
    private List<DirectRefService> directRefServices;

    private XTClientApplicationContext(XTClientApplicationContext.Builder builder) {
        this.application = builder.application;
        this.protocol = builder.protocol;
        this.directRefServices = builder.directRefServices;
    }

    public void start() {
        NettyClientStub stub = NettyClientStub.getInstance();
        directRefServices.stream().forEach((s) -> {
            Service service = new Service(s.getServiceInterface().getName(), s.getProtocol(),s.getAddress());
            stub.initRefService(service);
        });
    }

    public <T>T getRefService(Class<T> serviceInterface){
        return RPCClientProxy.getProxy(serviceInterface);
    }

    public static class Builder {

        private Application application;
        private String protocol;
        private List<DirectRefService> directRefServices = new ArrayList<>();

        public Builder() {
        }

        public Builder setApplication(Application application) {
            this.application = application;
            return this;
        }

        public Builder setProtocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder addRefService(Class<?> serviceInterface) {
            if(!serviceInterface.isInterface()) {
                throw new IllegalArgumentException("parameter must be interface");
            }
            return this;
        }

        public Builder addDirectRefService(DirectRefService service) {
            if(!service.getServiceInterface().isInterface()) {
                throw new IllegalArgumentException("parameter must be interface");
            }
            directRefServices.add(service);
            return this;
        }

        public XTClientApplicationContext build() {
            return new XTClientApplicationContext(this);
        }

    }
}
