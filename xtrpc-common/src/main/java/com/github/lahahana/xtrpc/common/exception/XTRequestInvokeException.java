package com.github.lahahana.xtrpc.common.exception;

public class XTRequestInvokeException extends RuntimeException {

    public XTRequestInvokeException() {
        super();
    }

    public XTRequestInvokeException(String message) {
        super(message);
    }

    public XTRequestInvokeException(String message, Throwable cause) {
        super(message, cause);
    }
}
