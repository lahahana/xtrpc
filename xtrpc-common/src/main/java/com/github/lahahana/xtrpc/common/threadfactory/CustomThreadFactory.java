package com.github.lahahana.xtrpc.common.threadfactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomThreadFactory implements ThreadFactory {

    private AtomicInteger counter = new AtomicInteger(1);

    private final String threadFactoryName;//used as thread name prefix

    private final boolean daemon;

    public CustomThreadFactory(String threadFactoryName) {
        this.threadFactoryName = threadFactoryName;
        this.daemon = true;
    }

    public CustomThreadFactory(String threadFactoryName, boolean daemon) {
        this.threadFactoryName = threadFactoryName;
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        int seq = counter.getAndIncrement();
        Thread t = new Thread(r,threadFactoryName+"-"+seq);
        t.setDaemon(daemon);
        return t;
    }
}
