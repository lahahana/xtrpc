package com.github.lahahana.xtrpc.client.handler;

import com.github.lahahana.xtrpc.client.ChannelHandlerCtxHolder;
import com.github.lahahana.xtrpc.client.dispatch.XTResponseDispatcher;
import com.github.lahahana.xtrpc.common.domain.XTResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class XTResponseInboundHandler extends SimpleChannelInboundHandler<XTResponse> {

    private static final Logger logger = LoggerFactory.getLogger(XTResponseInboundHandler.class);

    private static ChannelHandlerCtxHolder channelHandlerCtxHolder = ChannelHandlerCtxHolder.getInstance();

    private static XTResponseDispatcher responseDispatcher = XTResponseDispatcher.getInstance();

    private final String interfaceClazz;

    public XTResponseInboundHandler(String interfaceClazz) {
        this.interfaceClazz = interfaceClazz;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        channelHandlerCtxHolder.registerChannelHandlerContext(interfaceClazz, ctx);
        super.channelRegistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel connected:{} ", ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel unregistered:{} ", ctx.channel().remoteAddress());
        channelHandlerCtxHolder.removeChannelHandlerContext(interfaceClazz, ctx);
        super.channelUnregistered(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, XTResponse msg) throws Exception {
        logger.debug("receive response: XTResponse={}", msg);
        responseDispatcher.dispatch(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Exception:", cause);
        channelHandlerCtxHolder.removeChannelHandlerContext(interfaceClazz, ctx);
    }
}
