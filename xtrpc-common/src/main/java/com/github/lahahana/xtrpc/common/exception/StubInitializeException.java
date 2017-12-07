package com.github.lahahana.xtrpc.common.exception;

public class StubInitializeException extends RuntimeException {

    public StubInitializeException(String message) {
        super(message);
    }

    public StubInitializeException(String message, Throwable cause) {
        super(message, cause);
    }
}
