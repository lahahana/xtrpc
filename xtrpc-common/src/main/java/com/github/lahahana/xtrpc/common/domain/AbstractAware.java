package com.github.lahahana.xtrpc.common.domain;

import com.github.lahahana.xtrpc.common.exception.TimeoutException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public abstract class AbstractAware<R> implements Aware<R> {

    private Thread waiter;

    private R msg;

    @Override
    public R aware()  throws TimeoutException {
        return aware(0L);
    }

    @Override
    public R aware(long timeout) throws TimeoutException {
        waiter = Thread.currentThread();
        if (timeout <= 0) {
            LockSupport.park();
        } else {
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(timeout));
        }
//        if(waiter.isInterrupted()) {
//            throw new InterruptedException();
//        }
        if (msg == null) {
            throw new TimeoutException();
        }
//        synchronized (this) {
//            try {
//                this.wait(timeout);
//            } catch (InterruptedException e) {
//                throw new TimeoutException();
//            }
//        }
        return msg;
    }

    @Override
    public void notify(R msg) {
        this.msg = msg;
        LockSupport.unpark(waiter);
        waiter = null;
//        synchronized (this) {
//            notify();
//        }
    }
}
