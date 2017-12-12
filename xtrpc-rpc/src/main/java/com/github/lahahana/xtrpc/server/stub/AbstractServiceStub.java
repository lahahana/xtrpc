package com.github.lahahana.xtrpc.server.stub;

import com.github.lahahana.xtrpc.common.config.api.ServiceConfig;
import com.github.lahahana.xtrpc.common.util.NetworkUtil;

public abstract class AbstractServiceStub implements ServiceStub {

    protected final String inetHost = NetworkUtil.getLocalHostInetAddress().getHostAddress();//multi-network interface issue?

    protected final ServiceConfig serviceConfig;

    protected final Object serviceRef;

    public AbstractServiceStub(ServiceConfig serviceConfig) {
        this.serviceConfig = serviceConfig;
        this.serviceRef = serviceConfig.getRef();
    }

    public Object getServiceRef(){
        return serviceRef;
    };


    @Override
    public void destroy() {
        shutdown();
    }
}
