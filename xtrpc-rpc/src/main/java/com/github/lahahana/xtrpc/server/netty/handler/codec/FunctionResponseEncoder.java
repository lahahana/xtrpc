package com.github.lahahana.xtrpc.server.netty.handler.codec;

import com.github.lahahana.xtrpc.common.codec.CodecUtil;
import com.github.lahahana.xtrpc.common.constant.MessageConstraints;
import com.github.lahahana.xtrpc.common.domain.FunctionResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class FunctionResponseEncoder extends MessageToByteEncoder<FunctionResponse> {

    private static final Logger logger = LoggerFactory.getLogger(FunctionResponseEncoder.class);

    private CodecUtil codecUtil;

    public FunctionResponseEncoder(CodecUtil codecUtil) {
        this.codecUtil = codecUtil;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, FunctionResponse msg, ByteBuf out) throws Exception {
        logger.debug("prepare encode:{}", msg.toString());
        out.writeByte(MessageConstraints.FUNCTION_RESPONSE_HEAD);
        codecUtil.encode(out, msg);
    }
}
