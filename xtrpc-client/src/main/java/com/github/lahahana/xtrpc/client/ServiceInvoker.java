package com.github.lahahana.xtrpc.client;

import com.github.lahahana.xtrpc.common.domain.XTRequest;

public interface ServiceInvoker {

    public void invokeXTRequest(XTRequest request);

}
