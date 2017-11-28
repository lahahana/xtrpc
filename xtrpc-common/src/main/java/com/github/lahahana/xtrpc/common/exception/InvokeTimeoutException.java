package com.github.lahahana.xtrpc.common.exception;

/**
 * Used when call remote method timeout
 * */
public class InvokeTimeoutException extends RuntimeException {

    public InvokeTimeoutException(Throwable cause) {
        super(cause);
    }

    public InvokeTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
