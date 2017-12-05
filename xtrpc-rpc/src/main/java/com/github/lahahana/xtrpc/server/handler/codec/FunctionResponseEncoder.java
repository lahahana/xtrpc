package com.github.lahahana.xtrpc.server.handler.codec;

import com.github.lahahana.xtrpc.common.codec.CodecUtil;
import com.github.lahahana.xtrpc.common.codec.CodecUtilFactory;
import com.github.lahahana.xtrpc.common.constant.MessageConstraints;
import com.github.lahahana.xtrpc.common.domain.FunctionResponse;
import com.github.lahahana.xtrpc.common.domain.XTResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionResponseEncoder extends MessageToByteEncoder<FunctionResponse> {

    private static final Logger logger = LoggerFactory.getLogger(FunctionResponseEncoder.class);

    private static CodecUtil codecUtil = CodecUtilFactory.getCodecUtil();

    @Override
    protected void encode(ChannelHandlerContext ctx, FunctionResponse msg, ByteBuf out) throws Exception {
        logger.debug("prepare encode:{}", msg.toString());
        out.writeByte(MessageConstraints.FUNCTION_RESPONSE_HEAD);
        codecUtil.encode(out, msg);
    }
}
