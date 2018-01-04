package com.github.lahahana.xtrpc.common.codec;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

public interface CodecUtil {

    /**
     * Encode msg to bytes, byte format: codec_type + data_length + data
     */
    public void encode(ByteBuf byteBuf, Object msg) throws IOException;

    /**
     * Decode bytes to msg, byte format: codec_type + data_length + data
     */
    public Object decode(byte[] bytes) throws IOException;

}
