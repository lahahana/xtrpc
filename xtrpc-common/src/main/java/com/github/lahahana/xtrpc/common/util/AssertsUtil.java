package com.github.lahahana.xtrpc.common.util;

public class AssertsUtil {

    public static void ensureNotNull(String hint, Object object) {
        if (object == null) {
            throw new IllegalArgumentException(hint + " must not be null");
        }
    }
}
