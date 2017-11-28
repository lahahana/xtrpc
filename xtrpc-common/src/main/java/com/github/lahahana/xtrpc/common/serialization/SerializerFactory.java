package com.github.lahahana.xtrpc.common.serialization;

import com.github.lahahana.xtrpc.common.serialization.spi.Serializer;

public class SerializerFactory {

    private static Serializer serializer;

    public static Serializer getSerializer() {
        return serializer;
    }
}
