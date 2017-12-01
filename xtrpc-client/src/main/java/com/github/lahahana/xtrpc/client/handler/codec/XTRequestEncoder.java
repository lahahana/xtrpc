package com.github.lahahana.xtrpc.client.handler.codec;

import com.github.lahahana.xtrpc.common.codec.CodecUtilFactory;
import com.github.lahahana.xtrpc.common.codec.CodecUtil;
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
        logger.debug("prepare encode:{}", msg.toString());
        codecUtil.encode(out, msg);
    }
}
