package com.github.lahahana.xtrpc.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XTClientOutboundPortalHandler extends ChannelOutboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(XTClientOutboundPortalHandler.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        String serverAddress = ctx.channel().remoteAddress().toString();
        logger.debug("send request -> host={}, request={}", serverAddress, msg);
        super.write(ctx, msg, promise);
    }


}
