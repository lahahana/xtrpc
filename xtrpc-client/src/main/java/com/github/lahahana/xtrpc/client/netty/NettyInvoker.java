package com.github.lahahana.xtrpc.client.netty;

import com.github.lahahana.xtrpc.client.skeleton.AbstractInvoker;
import com.github.lahahana.xtrpc.common.domain.XTRequest;
import com.github.lahahana.xtrpc.common.exception.XTRequestInvokeException;
import com.github.lahahana.xtrpc.common.util.NetworkUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public final class NettyInvoker extends AbstractInvoker<ChannelHandlerContext> {

    private static final Logger logger = LoggerFactory.getLogger(NettyInvoker.class);

    public NettyInvoker(String interfaceName, ChannelHandlerContext channelHandlerContext) {
        super(interfaceName, channelHandlerContext);
    }

    @Override
    public String getAddress() {
        return NetworkUtil.assembleAddress((InetSocketAddress)pipe.channel().remoteAddress());
    }

    @Override
    protected void invokeXTRequest0(XTRequest xtRequest) throws XTRequestInvokeException {
        logger.debug("{}", xtRequest);
        Channel channel = pipe.channel();
        ChannelFuture channelFuture = channel.writeAndFlush(xtRequest);
    }

    public Channel channel(){
        return pipe.channel();
    }

    public ChannelHandlerContext channelHandlerContext(){
        return pipe;
    }
}
