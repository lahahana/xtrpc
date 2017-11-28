package com.github.lahahana.xtrpc.client.proxy;

import java.lang.reflect.Proxy;

public interface ProxyProvider {

    public <T> T getProxy(Class<T> clazz);

}
