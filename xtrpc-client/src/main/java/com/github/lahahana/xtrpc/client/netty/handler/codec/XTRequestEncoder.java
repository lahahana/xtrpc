package com.github.lahahana.xtrpc.client.netty.handler.codec;

import com.github.lahahana.xtrpc.common.codec.CodecUtil;
import com.github.lahahana.xtrpc.common.codec.CodecUtilFactory;
import com.github.lahahana.xtrpc.common.constant.MessageConstraints;
import com.github.lahahana.xtrpc.common.domain.XTRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XTRequestEncoder extends MessageToByteEncoder<XTRequest> {

    private static final Logger logger = LoggerFactory.getLogger(XTRequestEncoder.class);

    private static CodecUtil codecUtil = CodecUtilFactory.getCodecUtil();

    @Override
    protected void encode(ChannelHandlerContext ctx, XTRequest msg, ByteBuf out) throws Exception {
        if(out.isWritable()) {
            out.writeByte(MessageConstraints.XTREQUEST_HEAD);
        }
        codecUtil.encode(out, msg);
    }
}
