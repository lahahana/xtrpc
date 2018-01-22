package com.github.lahahana.xtrpc.client.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class XTClientOutboundPortalHandler extends ChannelOutboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(XTClientOutboundPortalHandler.class);

    private final String interfaceClazz;

    public XTClientOutboundPortalHandler(String interfaceClazz) {
        this.interfaceClazz = interfaceClazz;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        String serverAddress = ctx.channel().remoteAddress().toString();
        logger.debug("start send request -> host={}, request={}", serverAddress, msg);
        super.write(ctx, msg, promise);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Exception:", cause);
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        logger.debug("Disconnected: remote address = {}", ctx.channel().remoteAddress());
        super.disconnect(ctx, promise);
    }
}
