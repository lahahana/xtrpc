package com.github.lahahana.xtrpc.client.skeleton;

import com.github.lahahana.xtrpc.common.domain.XTRequest;
import com.github.lahahana.xtrpc.common.exception.XTRequestInvokeException;

public interface Invoker {

    public void invokeXTRequest(XTRequest request) throws XTRequestInvokeException;

    public String getInterface();

    public String getAddress();

    public void markAsAvailable();

    public void markAsUnavailable();

    public void incrementInvokeCount();

    public long getInvokeCount();

}
