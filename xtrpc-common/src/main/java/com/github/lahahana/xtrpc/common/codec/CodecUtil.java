package com.github.lahahana.xtrpc.common.codec;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

public interface CodecUtil {

    public void encode(ByteBuf byteBuf, Object msg) throws IOException;

    public Object decode(byte[] bytes) throws IOException;

    public int getByteNumOfDataLengthMark();
}
