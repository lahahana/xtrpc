package com.github.lahahana.xtrpc.common.codec;

public class CodecUtilFactory {

    public static CodecUtil getCodecUtil() {
        //TO-DO get codec util by serialization scheme
        return new KryoCodecUtil();
    }
}
