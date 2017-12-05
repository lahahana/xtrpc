package com.github.lahahana.xtrpc.server.handler;

import com.github.lahahana.xtrpc.common.domain.XTRequest;
import com.github.lahahana.xtrpc.common.util.Mock;
import com.github.lahahana.xtrpc.server.ChannelHandlerCtxHolder;
import com.github.lahahana.xtrpc.server.dispatch.XTRequestDispatcher;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class XTRequestInboundHandler extends SimpleChannelInboundHandler<XTRequest> {

    private static final Logger logger = LoggerFactory.getLogger(XTRequestInboundHandler.class);

    private static ChannelHandlerCtxHolder channelHandlerCtxHolder = ChannelHandlerCtxHolder.getInstance();

    private static XTRequestDispatcher dispatcher = new XTRequestDispatcher();

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
            logger.debug("receive request: srcHost={}, XTRequest={}", clientHost, msg);
            //dispatch request to local function in async
            dispatcher.dispatch(ctx.channel(), msg);
        } catch (Exception e) {
            logger.error("fail to dispatch request: XTRequest={}", msg, e);
        } finally {
            removeChannelHandlerCtx(ctx);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("exception caught:", cause);
    }

    private void removeChannelHandlerCtx(ChannelHandlerContext ctx) {
        channelHandlerCtxHolder.removeChannelHandlerCtx(ctx);
    }

    @Mock
    public void addMockService(String serviceName, Object object) {
        dispatcher.addMockService(serviceName, object);
    }
}
