package com.github.lahahana.xtrpc.common.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class CommonUtil {

    public static String getStackTraceFromThrowable(Throwable throwable) {
        try(StringWriter stackTraceWriter = new StringWriter();PrintWriter printWriter = new PrintWriter(stackTraceWriter);){
            throwable.printStackTrace(printWriter);
            return stackTraceWriter.toString();
        } catch (IOException e){
            return null;
        }
    }
}
