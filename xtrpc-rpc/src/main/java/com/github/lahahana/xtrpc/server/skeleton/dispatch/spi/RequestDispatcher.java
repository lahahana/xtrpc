package com.github.lahahana.xtrpc.server.skeleton.dispatch.spi;

import com.github.lahahana.xtrpc.common.domain.XTRequest;

import java.util.concurrent.Future;

public interface RequestDispatcher<T> {

    /**
     * Dispatch request to target service existed in spring context, etc.
     */
    public Future dispatch(T invoker, XTRequest xtRequest);
}
