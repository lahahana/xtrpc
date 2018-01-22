package com.github.lahahana.xtrpc.common.domain;

import com.github.lahahana.xtrpc.common.exception.TimeoutException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public abstract class AbstractAware<R> implements Aware<R> {

    private Thread waiter;

    private boolean awared;

    private R msg;

    @Override
    public R aware() {
        waiter = Thread.currentThread();
        LockSupport.park();
        return msg;
    }

    @Override
    public R aware(long timeout) throws TimeoutException {
        waiter = Thread.currentThread();

        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(timeout));
        if (!awared) {
            throw new TimeoutException();
        }
        return msg;
    }

    @Override
    public void notify(R msg) {
        this.awared = true;
        this.msg = msg;
        LockSupport.unpark(waiter);

        //release thread reference
        waiter = null;
    }
}
