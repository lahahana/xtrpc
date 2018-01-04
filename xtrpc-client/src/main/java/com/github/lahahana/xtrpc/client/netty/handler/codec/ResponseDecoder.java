package com.github.lahahana.xtrpc.client.netty.handler.codec;

import com.github.lahahana.xtrpc.common.codec.CodecUtil;
import com.github.lahahana.xtrpc.common.codec.CodecUtilFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ResponseDecoder extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(ResponseDecoder.class);

    private CodecUtil codecUtil;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.isReadable()) {
            in.markReaderIndex();
            byte msgType = in.readByte();
            byte codecType = in.readByte();
            //get corresponding codec util by msg codec type at first time
            codecUtil = codecUtil == null ? CodecUtilFactory.getCodecUtil(codecType) : codecUtil;

            if (in.readableBytes() < codecUtil.getByteNumOfDataLengthMark()) {
                in.resetReaderIndex();
                return;
            }
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
