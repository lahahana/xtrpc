package com.github.lahahana.xtrpc.client.handler.codec;

import com.github.lahahana.xtrpc.common.codec.CodecUtilFactory;
import com.github.lahahana.xtrpc.common.codec.CodecUtil;
import com.github.lahahana.xtrpc.common.constant.MessageConstraints;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ResponseDecoder extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(ResponseDecoder.class);

    private static CodecUtil codecUtil = CodecUtilFactory.getCodecUtil();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.isReadable()) {
            byte msgType = in.readByte();
            if (in.readableBytes() < codecUtil.getByteNumOfDataLengthMark()) {
                return;
            }
            in.markReaderIndex();
            int dataLength = in.readInt();
            if (in.readableBytes() < dataLength) {
                in.resetReaderIndex();
                return;
            }
            byte[] data = new byte[dataLength];
            in.readBytes(data);
            Object result = codecUtil.decode(data);
            out.add(result);
        }
    }
}
