package com.github.lahahana.xtrpc.client.proxy;

public class RPCClientProxy {

    private static ProxyProvider proxyProvider = new JdkProxyProvider();

    public static <T> T getProxy(Class<T> clazz) {
        return proxyProvider.getProxy(clazz);
    }

}
