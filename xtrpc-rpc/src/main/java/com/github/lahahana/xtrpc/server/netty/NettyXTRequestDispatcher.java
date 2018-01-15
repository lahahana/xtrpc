package com.github.lahahana.xtrpc.server.netty;

import com.github.lahahana.xtrpc.common.constant.MessageConstraints;
import com.github.lahahana.xtrpc.common.domain.XTResponse;
import com.github.lahahana.xtrpc.server.skeleton.dispatch.spi.XTRequestDispatcher;
import io.netty.channel.Channel;

import java.io.IOException;

public class NettyXTRequestDispatcher extends XTRequestDispatcher<Channel> {

    public NettyXTRequestDispatcher(Object serviceRef) {
        super(serviceRef);
    }

    @Override
    public void sendResponse(Channel channel, XTResponse xtResponse) throws IOException {
        channel.write(MessageConstraints.XTRESPONSE_HEAD);
        channel.writeAndFlush(xtResponse);
    }
}
