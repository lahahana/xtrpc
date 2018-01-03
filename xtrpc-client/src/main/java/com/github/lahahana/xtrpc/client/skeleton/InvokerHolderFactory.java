package com.github.lahahana.xtrpc.client.skeleton;

import com.github.lahahana.xtrpc.client.netty.NettyInvokerHolder;
import com.github.lahahana.xtrpc.common.base.SingletonDestroyableFactory;

import java.util.HashSet;
import java.util.Set;

import static com.github.lahahana.xtrpc.common.constant.Constraints.Transporter;

public class InvokerHolderFactory extends SingletonDestroyableFactory {

    private static volatile InvokerHolderFactory instance;

    private Set<InvokerHolder> invokerHolders = new HashSet<>();

    public static InvokerHolderFactory getInstance() {
        if (instance == null) {
            synchronized (InvokerHolderFactory.class) {
                if (instance == null) {
                    instance = new InvokerHolderFactory();
                }
            }
        }
        return instance;
    }

    public synchronized InvokerHolder getInvokerHolder(Transporter transporter) {
        InvokerHolder invokerHolder = null;
        switch (transporter) {
            case NETTY:
                invokerHolder = NettyInvokerHolder.getInstance();
                invokerHolders.add(invokerHolder);
                break;
            default:
                throw new IllegalArgumentException("unknown transporter type" + transporter);
        }

        return invokerHolder;
    }

    @Override
    public void destroy() {
        invokerHolders.parallelStream().forEach((invokerHolder) -> invokerHolder.destroy());
    }
}
