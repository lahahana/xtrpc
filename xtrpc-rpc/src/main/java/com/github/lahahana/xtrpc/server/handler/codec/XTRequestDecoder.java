package com.github.lahahana.xtrpc.server.handler.codec;

import com.github.lahahana.xtrpc.common.codec.CodecUtil;
import com.github.lahahana.xtrpc.common.codec.CodecUtilFactory;
import com.github.lahahana.xtrpc.common.constant.MessageConstraints;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class XTRequestDecoder extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(XTRequestDecoder.class);

    private static CodecUtil codecUtil = CodecUtilFactory.getCodecUtil();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        byte headMark = in.readByte();
        if(headMark == MessageConstraints.HEART_BEAT_HEAD) {
            //expand the expiration time of channel
            logger.debug("receive heart beat from: {}", ctx.channel().remoteAddress());
            return;
        }

        //reset read index
        in.resetReaderIndex();

        if(in.readableBytes() < codecUtil.getByteNumOfDataLengthMark()) {
            return;
        }

        in.markReaderIndex();
        int dataLength = in.readInt();

        //wait for full-message
        if(in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }

        byte[] data = new byte[dataLength];
        in.readBytes(data);
        Object result = codecUtil.decode(data);
        out.add(result);
    }
}
