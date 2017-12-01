package com.github.lahahana.xtrpc.server.handler.codec;

import com.github.lahahana.xtrpc.common.codec.CodecUtilFactory;
import com.github.lahahana.xtrpc.common.codec.CodecUtil;
import com.github.lahahana.xtrpc.common.domain.XTResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class XTResponseEncoder extends MessageToByteEncoder<XTResponse> {

    private static final Logger logger = LoggerFactory.getLogger(XTResponseEncoder.class);

    private static CodecUtil codecUtil = CodecUtilFactory.getCodecUtil();

    @Override
    protected void encode(ChannelHandlerContext ctx, XTResponse msg, ByteBuf out) throws Exception {
        logger.debug("prepare encode:{}", msg.toString());
        codecUtil.encode(out, msg);
    }
}
