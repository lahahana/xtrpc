package com.github.lahahana.xtrpc.client.netty;

import com.github.lahahana.xtrpc.client.skeleton.invoker.AbstractInvokerHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyInvokerHolder extends AbstractInvokerHolder {

    private static final Logger logger = LoggerFactory.getLogger(NettyInvokerHolder.class);

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
