package com.github.lahahana.xtrpc.client;

import com.github.lahahana.xtrpc.common.domain.XTRequest;
import com.google.protobuf.Service;
import io.netty.channel.ChannelHandlerContext;

public class NettyServiceInvoker implements ServiceInvoker {

    private ChannelHandlerContext channelHandlerContext;

    public NettyServiceInvoker(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    @Override
    public void invokeXTRequest(XTRequest request) {
        channelHandlerContext.channel().writeAndFlush(request);
    }
}
