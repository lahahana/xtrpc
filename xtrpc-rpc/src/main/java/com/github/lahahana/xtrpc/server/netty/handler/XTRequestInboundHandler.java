package com.github.lahahana.xtrpc.server.netty.handler;

import com.github.lahahana.xtrpc.common.domain.XTRequest;
import com.github.lahahana.xtrpc.server.netty.ChannelHandlerCtxHolder;
import com.github.lahahana.xtrpc.server.netty.NettyXTRequestDispatcher;
import com.github.lahahana.xtrpc.server.skeleton.dispatch.spi.XTRequestDispatcher;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class XTRequestInboundHandler extends SimpleChannelInboundHandler<XTRequest> {

    private static final Logger logger = LoggerFactory.getLogger(XTRequestInboundHandler.class);

    private static ChannelHandlerCtxHolder channelHandlerCtxHolder = ChannelHandlerCtxHolder.getInstance();

    private XTRequestDispatcher dispatcher;

    private Object serviceRef;

    public XTRequestInboundHandler(Object serviceRef) {
        this.serviceRef = serviceRef;
        this.dispatcher = new NettyXTRequestDispatcher(serviceRef);

    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel registered: socket address={} ", ctx.channel().remoteAddress());
        channelHandlerCtxHolder.registerChannel(ctx);
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel unregistered: socket address={} ", ctx.channel().remoteAddress());
        removeChannelHandlerCtx(ctx);
        super.channelUnregistered(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, XTRequest msg) throws Exception {
        try {
            String clientHost = ctx.channel().remoteAddress().toString();
            logger.debug("srcHost={}, XTRequest={}", clientHost, msg);
            //dispatch request to local function async
            dispatcher.dispatch(ctx.channel(), msg);
        } catch (Exception e) {
            logger.error("fail to dispatch request: XTRequest={}", msg, e);
        } finally {
            removeChannelHandlerCtx(ctx);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("exception caught:", cause);
    }

    private void removeChannelHandlerCtx(ChannelHandlerContext ctx) {
        channelHandlerCtxHolder.removeChannelHandlerCtx(ctx);
    }

}
