package com.github.lahahana.xtrpc.client.netty.handler;

import com.github.lahahana.xtrpc.client.dispatch.XTResponseDispatcher;
import com.github.lahahana.xtrpc.common.domain.Aware;
import com.github.lahahana.xtrpc.common.domain.XTResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class XTResponseInboundHandler extends SimpleChannelInboundHandler<XTResponse> {

    private static final Logger logger = LoggerFactory.getLogger(XTResponseInboundHandler.class);

    private static XTResponseDispatcher responseDispatcher = XTResponseDispatcher.getInstance();

    private final String interfaceClazz;

    private Aware registerAware;

    public XTResponseInboundHandler(String interfaceClazz) {
        this.interfaceClazz = interfaceClazz;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, XTResponse msg) throws Exception {
        logger.debug("receive response: XTResponse={}", msg);
        responseDispatcher.dispatch(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Exception:", cause);
    }
}
