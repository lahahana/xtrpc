package com.github.lahahana.xtrpc.server.dispatch.spi;

import com.github.lahahana.xtrpc.common.exception.ServiceNotFoundException;
import com.github.lahahana.xtrpc.common.domain.XTRequest;
import io.netty.channel.Channel;

import java.util.concurrent.Future;

public interface RequestDispatcher {

    /**
     * Dispatch request to target service existed in spring context, etc.
     * */
    public Future dispatch(Channel channel, XTRequest xtRequest);
}
