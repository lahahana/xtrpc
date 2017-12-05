package com.github.lahahana.xtrpc.client.handler.codec;

import com.github.lahahana.xtrpc.common.codec.CodecUtil;
import com.github.lahahana.xtrpc.common.codec.CodecUtilFactory;
import com.github.lahahana.xtrpc.common.constant.MessageConstraints;
import com.github.lahahana.xtrpc.common.domain.FunctionRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


public class FunctionRequestEncoder extends MessageToByteEncoder<FunctionRequest> {

    private static CodecUtil codecUtil = CodecUtilFactory.getCodecUtil();

    @Override
    protected void encode(ChannelHandlerContext ctx, FunctionRequest msg, ByteBuf out) throws Exception {
        if(out.isWritable()) {
            out.writeByte(MessageConstraints.FUNCTION_REQUEST_HEAD);
        }
        codecUtil.encode(out, msg);
    }
}
