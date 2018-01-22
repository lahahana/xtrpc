package com.github.lahahana.xtrpc.client.skeleton.proxy;

import com.github.lahahana.xtrpc.common.config.api.Protocol;

public interface ProxyProvider {

    public <T> T getProxy(Class<T> clazz, Protocol protocol);

}
