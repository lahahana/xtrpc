package com.github.lahahana.xtrpc.common.exception;

public class RejectInvokeException extends RuntimeException {

    public RejectInvokeException() {
        super();
    }

    public RejectInvokeException(String message) {
        super(message);
    }

    public RejectInvokeException(String message, Throwable cause) {
        super(message, cause);
    }

    public RejectInvokeException(Throwable cause) {
        super(cause);
    }
}
