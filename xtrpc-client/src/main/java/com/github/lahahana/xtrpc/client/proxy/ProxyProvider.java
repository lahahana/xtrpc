package com.github.lahahana.xtrpc.client.proxy;

public interface ProxyProvider {

    public <T> T getProxy(Class<T> clazz);

}
