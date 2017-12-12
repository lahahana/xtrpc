package com.github.lahahana.xtrpc.client.skeleton;

import com.github.lahahana.xtrpc.common.domain.XTRequest;
import com.github.lahahana.xtrpc.common.exception.XTRequestInvokeException;

import java.util.concurrent.atomic.LongAdder;

public abstract class AbstractInvoker<T> implements Invoker {

    private String interfaceName;

    protected T pipe;

    private volatile boolean available;

    private LongAdder invokeCounter = new LongAdder();

    public AbstractInvoker(String interfaceName, T pipe) {
        this.interfaceName = interfaceName;
        this.pipe = pipe;
        this.available = true;
    }

    protected abstract void invokeXTRequest0(XTRequest request) throws XTRequestInvokeException;

    @Override
    public final void invokeXTRequest(XTRequest request) throws XTRequestInvokeException {
        incrementInvokeCount();
        invokeXTRequest0(request);
    }

    @Override
    public String getInterface() {
        return interfaceName;
    }

    @Override
    public void markAsAvailable() {
        available = true;
    }

    @Override
    public void markAsUnavailable() {
        available = false;
    }

    @Override
    public void incrementInvokeCount() {
        invokeCounter.increment();
    }

    @Override
    public long getInvokeCount() {
        return invokeCounter.longValue();
    }

    @Override
    public String toString() {
        return "[Invoker]:interface=" + interfaceName + ",pipe=" + pipe + ",invokeCount=" + invokeCounter.longValue();
    }
}
