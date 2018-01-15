package com.github.lahahana.xtrpc.common.codec;

import com.github.lahahana.xtrpc.common.constant.Constraints.Serialization;

import static com.github.lahahana.xtrpc.common.constant.MessageConstraints.CODEC_JAVA;
import static com.github.lahahana.xtrpc.common.constant.MessageConstraints.CODEC_KRYO;

public class CodecUtilFactory {

    private static final KryoCodecUtil kryoCodecUtil = new KryoCodecUtil();

    private static final JavaCodecUtil javaCodecUtil = new JavaCodecUtil();

    public static CodecUtil getCodecUtil(String codecType) {
        if (codecType == null)
            return kryoCodecUtil;
        Serialization codec = Serialization.valueOf(codecType);
        switch (codec) {
            case KRYO:
                return kryoCodecUtil;
            case JAVA:
                return javaCodecUtil;
            default:
                throw new IllegalArgumentException("unknown codec type" + codecType);
        }
    }

    public static CodecUtil getCodecUtil(byte codecType) {
        switch (codecType) {
            case CODEC_KRYO:
                return kryoCodecUtil;
            case CODEC_JAVA:
                return javaCodecUtil;
            default:
                throw new IllegalArgumentException("unknown codec type" + codecType);
        }
    }
}
