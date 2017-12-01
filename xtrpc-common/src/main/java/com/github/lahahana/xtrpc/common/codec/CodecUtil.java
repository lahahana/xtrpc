package com.github.lahahana.xtrpc.common.codec;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

public interface CodecUtil {


    static final byte DATA_SIZE_HEAD_LENGTH = 4;

    static final byte HEAD_MARK_LENGTH = 1;

    public void encode(ByteBuf byteBuf, Object msg) throws IOException;

    public Object decode(byte[] bytes) throws IOException;

    public default int getByteNumOfDataLengthMark() {
        return DATA_SIZE_HEAD_LENGTH;
    }

    public default int getByteNumOfHeadMark() {
        return HEAD_MARK_LENGTH;
    }
}
