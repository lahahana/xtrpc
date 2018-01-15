package com.github.lahahana.xtrpc.server.netty.handler;

import com.github.lahahana.xtrpc.common.constant.MessageConstraints;
import com.github.lahahana.xtrpc.common.domain.FunctionRequest;
import com.github.lahahana.xtrpc.common.domain.FunctionResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionRequestInboundHandler extends SimpleChannelInboundHandler<FunctionRequest> {

    private static final Logger logger = LoggerFactory.getLogger(FunctionRequestInboundHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FunctionRequest msg) throws Exception {
        try {
            String clientHost = ctx.channel().remoteAddress().toString();
            logger.debug("receive function request: srcHost={}, FunctionRequest={}", clientHost, msg);
            //process heart beat,etc. on IO thread directly
            FunctionResponse functionResponse = new FunctionResponse(MessageConstraints.FUNCTION_HEART_BEAT_HEAD);
            ctx.channel().writeAndFlush(functionResponse);
        } catch (Exception e) {
            logger.error("fail to process request: FunctionRequest={}", msg, e);
        }
    }

}
