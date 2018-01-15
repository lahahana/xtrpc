package com.github.lahahana.xtrpc.client.skeleton.invoker;

import com.github.lahahana.xtrpc.common.base.Destroyable;
import com.github.lahahana.xtrpc.common.domain.XTRequest;
import com.github.lahahana.xtrpc.common.exception.XTRequestInvokeException;

public interface Invoker extends Destroyable {

    public void invokeXTRequest(XTRequest request) throws XTRequestInvokeException;

    public String getInterface();

    public String getAddress();

    public void markAsAvailable();

    public void markAsUnavailable();

    public boolean isAvailable();

    public void incrementInvokeCount();

    public long getInvokeCount();

}
