package com.github.lahahana.xtrpc.common.codec;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.github.lahahana.xtrpc.common.constant.MessageConstraints;
import com.github.lahahana.xtrpc.common.serialization.KryoPoolFactory;
import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KryoCodecUtil implements CodecUtil {

    private static KryoPool pool = KryoPoolFactory.getInstance().getKryoPool();

    @Override
    public void encode(ByteBuf byteBuf, Object msg) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Kryo kryo = pool.borrow();
        Output out = new Output(outputStream);
        kryo.writeClassAndObject(out, msg);
        out.close();
        pool.release(kryo);

        //write codec type ahead
        byteBuf.writeByte(MessageConstraints.CODEC_KRYO);
        //write data length ahead
        byte[] bytesOfMsg = outputStream.toByteArray();
        byteBuf.writeInt(bytesOfMsg.length);
        byteBuf.writeBytes(bytesOfMsg);

    }

    @Override
    public Object decode(byte[] bytes) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        Kryo kryo = pool.borrow();
        Input input = new Input(inputStream);
        Object object = kryo.readClassAndObject(input);
        input.close();
        pool.release(kryo);
        return object;
    }
}
