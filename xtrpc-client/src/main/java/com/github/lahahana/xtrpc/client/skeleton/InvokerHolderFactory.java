package com.github.lahahana.xtrpc.client.skeleton;

import com.github.lahahana.xtrpc.client.netty.NettyInvokerHolder;

public class InvokerHolderFactory {

    private volatile static NettyInvokerHolder nettyInvokerHolder;

    public synchronized static InvokerHolder getInvokerHolder() {
        nettyInvokerHolder = NettyInvokerHolder.getInstance();
        return nettyInvokerHolder;
    }
}
