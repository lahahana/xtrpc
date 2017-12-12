package com.github.lahahana.xtrpc.client.proxy;

import com.github.lahahana.xtrpc.common.config.api.Protocol;

public class XTRpcClientProxy {

    private static ProxyProvider proxyProvider = new JdkProxyProvider();

    public static <T> T getProxy(Class<T> clazz, Protocol protocol) {
        return proxyProvider.getProxy(clazz, protocol);
    }

}
