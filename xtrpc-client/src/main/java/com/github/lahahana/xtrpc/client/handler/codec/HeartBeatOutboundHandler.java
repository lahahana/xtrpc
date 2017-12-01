package com.github.lahahana.xtrpc.client.handler.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartBeatOutboundHandler extends ChannelOutboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(HeartBeatOutboundHandler.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        logger.debug("walk through");
        ctx.writeAndFlush(msg, promise);
    }
}
