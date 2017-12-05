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

public class RequestDecoder extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(RequestDecoder.class);

    private static CodecUtil codecUtil = CodecUtilFactory.getCodecUtil();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        byte headMark = in.readByte();
        if(headMark == MessageConstraints.FUNCTION_REQUEST_HEAD) {
            //expand the expiration time of channel
            //update channel expiration time, to avoid unlimited channel hold resource?
            logger.debug("receive FunctionRequest from: {}", ctx.channel().remoteAddress());
            if(in.readableBytes() < codecUtil.getByteNumOfDataLengthMark()) {
                in.resetReaderIndex();
                return;
            }
            in.markReaderIndex();
            int dataLength = in.readInt();
            if(in.readableBytes() < dataLength) {
                in.resetReaderIndex();
                return;
            }
            byte[] data = new byte[dataLength];
            in.readBytes(data);
            Object result = codecUtil.decode(data);
            out.add(result);
        }else if(headMark == MessageConstraints.XTREQUEST_HEAD) {
            logger.debug("receive XTRequest from: {}", ctx.channel().remoteAddress());
            if(in.readableBytes() < codecUtil.getByteNumOfDataLengthMark()) {
                in.resetReaderIndex();
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
        }else {
            //reset read index
            in.resetReaderIndex();
        }
    }
}
