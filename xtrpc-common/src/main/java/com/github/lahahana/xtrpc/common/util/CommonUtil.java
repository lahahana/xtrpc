package com.github.lahahana.xtrpc.common.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
import java.util.stream.Stream;

public class CommonUtil {

    public static String getStackTraceFromThrowable(Throwable throwable) {
        try (StringWriter stackTraceWriter = new StringWriter(); PrintWriter printWriter = new PrintWriter(stackTraceWriter);) {
            throwable.printStackTrace(printWriter);
            return stackTraceWriter.toString();
        } catch (IOException e) {
            return null;
        }
    }

    public static Optional<? extends Class<?>> findMatchedExceptionType(Class<?>[] exceptionTypes, Class<?> clazz) {
        return Stream.of(exceptionTypes).filter((c) -> c.equals(clazz) || clazz.isAssignableFrom(c)).findFirst();
    }
}
