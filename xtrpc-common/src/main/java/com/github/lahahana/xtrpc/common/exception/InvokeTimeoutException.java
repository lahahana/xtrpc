package com.github.lahahana.xtrpc.common.exception;

/**
 * Used when call remote method timeout
 * */
public class InvokeTimeoutException extends RuntimeException {

    public InvokeTimeoutException(String message) {
        super(message);
    }

    public InvokeTimeoutException(Throwable cause) {
        super(cause);
    }

    public InvokeTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
