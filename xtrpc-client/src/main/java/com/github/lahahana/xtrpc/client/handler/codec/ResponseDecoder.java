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
            in.markReaderIndex();
            byte header = in.readByte();
            //check heart beat response
            if (header == MessageConstraints.FUNCTION_RESPONSE_HEAD) {
                logger.debug("receive function response on channel:{}", ctx.channel());
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
            } else if (header == MessageConstraints.XTRESPONSE_HEAD) {
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

            } else {
                in.resetReaderIndex();
            }
        }
    }
}
