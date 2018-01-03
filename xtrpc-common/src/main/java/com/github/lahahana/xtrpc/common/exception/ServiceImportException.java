package com.github.lahahana.xtrpc.common.exception;

public class ServiceImportException extends Exception {
    public ServiceImportException() {
        super();
    }

    public ServiceImportException(String message) {
        super(message);
    }

    public ServiceImportException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceImportException(Throwable cause) {
        super(cause);
    }

    protected ServiceImportException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
