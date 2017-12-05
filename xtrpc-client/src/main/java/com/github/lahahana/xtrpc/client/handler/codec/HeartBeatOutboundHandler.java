package com.github.lahahana.xtrpc.client.handler.codec;

import com.github.lahahana.xtrpc.client.ChannelHandlerCtxHolder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Deprecated
@ChannelHandler.Sharable
public class HeartBeatOutboundHandler extends ChannelOutboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(HeartBeatOutboundHandler.class);

    private static ChannelHandlerCtxHolder channelHandlerCtxHolder = ChannelHandlerCtxHolder.getInstance();

    public HeartBeatOutboundHandler() {
        super();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        logger.debug("send heart beat on channel:{}", ctx.channel());
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer(1);
        byteBuf.writeByte((byte)msg);
        ctx.writeAndFlush(byteBuf, promise);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Exception:", cause);
        channelHandlerCtxHolder.removeChannelHandlerContext(ctx);
    }
}
