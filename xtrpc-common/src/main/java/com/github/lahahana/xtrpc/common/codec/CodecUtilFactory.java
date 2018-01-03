package com.github.lahahana.xtrpc.common.codec;

public class CodecUtilFactory {

    private static KryoCodecUtil kryoCodecUtil = new KryoCodecUtil();

    public static CodecUtil getCodecUtil(String codec) {
        switch (codec) {
            case "kryo":
                return kryoCodecUtil;
            default:
                throw new IllegalArgumentException("unknown codec type" + codec);
        }
    }

    public static CodecUtil getCodecUtil(byte codec) {
        switch (codec) {
            case 1:
                return kryoCodecUtil;
            default:
                throw new IllegalArgumentException("unknown codec type" + codec);
        }
    }
}
