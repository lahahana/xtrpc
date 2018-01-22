package com.github.lahahana.xtrpc.client.netty.handler;

import com.github.lahahana.xtrpc.common.constant.MessageConstraints;
import com.github.lahahana.xtrpc.common.domain.FunctionResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionResponseInboundHandler extends SimpleChannelInboundHandler<FunctionResponse> {

    private static final Logger logger = LoggerFactory.getLogger(FunctionResponseInboundHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FunctionResponse msg) throws Exception {
        if (msg.getType() == MessageConstraints.FUNCTION_HEART_BEAT_HEAD) {
            logger.debug("receive heart beat response: FunctionResponse={}", msg);
            //mark target channel is healthy
        }
    }
}
