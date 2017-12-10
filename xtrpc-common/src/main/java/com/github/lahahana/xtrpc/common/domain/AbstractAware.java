package com.github.lahahana.xtrpc.common.domain;

import com.github.lahahana.xtrpc.common.exception.TimeoutException;

public abstract class AbstractAware<R> implements Aware<R> {

    private R msg;

    @Override
    public R aware() {
        return msg;
    }

    @Override
    public R aware(long timeout) throws TimeoutException {
        synchronized (this) {
            try {
                this.wait(timeout);
            } catch (InterruptedException e) {
                throw new TimeoutException();
            }
        }
        return msg;
    }

    @Override
    public void notify(R msg) {
        this.msg = msg;
        synchronized (this) {
            notify();
        }
    }
}
