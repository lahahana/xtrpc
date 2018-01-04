package com.github.lahahana.xtrpc.common.codec;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

public interface CodecUtil {


    static final byte DATA_SIZE_HEAD_LENGTH = 4;

    static final byte HEAD_MARK_LENGTH = 1;

    /**
     * Encode msg to bytes, byte format: codec_type + data_length + data
     */
    public void encode(ByteBuf byteBuf, Object msg) throws IOException;

    /**
     * Decode bytes to msg, byte format: codec_type + data_length + data
     */
    public Object decode(byte[] bytes) throws IOException;

    public default int getByteNumOfDataLengthMark() {
        return DATA_SIZE_HEAD_LENGTH;
    }

    public default int getByteNumOfHeadMark() {
        return HEAD_MARK_LENGTH;
    }
}
