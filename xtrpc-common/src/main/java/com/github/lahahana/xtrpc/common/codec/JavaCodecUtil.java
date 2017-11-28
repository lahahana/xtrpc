package com.github.lahahana.xtrpc.common.codec;

import io.netty.buffer.ByteBuf;

import java.io.*;

public class JavaCodecUtil implements CodecUtil {

    private static final byte HEAD_LENGTH = 4;

    @Override
    public void encode(ByteBuf byteBuf, Object msg) throws IOException {
        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(outputStream)) {
            oos.writeObject(msg);
            byte[] bytesOfMsg = outputStream.toByteArray();
            byteBuf.writeInt(bytesOfMsg.length);
            byteBuf.writeBytes(bytesOfMsg);
        }

    }

    @Override
    public Object decode(byte[] bytes) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes); ObjectInputStream ois = new ObjectInputStream(inputStream)) {
                return ois.readObject();

        } catch (ClassNotFoundException e) {
            throw new IOException("class not found exception", e);
        }
    }

    @Override
    public int getByteNumOfDataLengthMark() {
        return HEAD_LENGTH;
    }
}
