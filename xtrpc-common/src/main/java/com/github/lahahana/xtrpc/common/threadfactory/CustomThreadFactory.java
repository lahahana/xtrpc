package com.github.lahahana.xtrpc.common.threadfactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomThreadFactory implements ThreadFactory {

    private AtomicInteger counter = new AtomicInteger(1);

    private final String threadFactoryName;//used as thread name prefix

    public CustomThreadFactory(String threadFactoryName) {
        this.threadFactoryName = threadFactoryName;
    }

    @Override
    public Thread newThread(Runnable r) {
        int seq = counter.getAndIncrement();
        return new Thread(r,threadFactoryName+"-"+seq);
    }
}
