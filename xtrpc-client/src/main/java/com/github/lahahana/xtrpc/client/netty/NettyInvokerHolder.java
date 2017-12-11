package com.github.lahahana.xtrpc.client.netty;

import com.github.lahahana.xtrpc.client.skeleton.AbstractInvokerHolder;

public class NettyInvokerHolder extends AbstractInvokerHolder {

    private static volatile NettyInvokerHolder instance;

    private NettyInvokerHolder(){}

    public static NettyInvokerHolder getInstance() {
        if (instance == null) {
            synchronized (NettyInvokerHolder.class) {
                if (instance == null) {
                    instance = new NettyInvokerHolder();
                }
            }
        }
        return instance;
    }
}
