package com.github.lahahana.xtrpc.common.exception;

public class ServiceExportException extends Exception {

    public ServiceExportException() {
        super();
    }

    public ServiceExportException(String message) {
        super(message);
    }

    public ServiceExportException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceExportException(Throwable cause) {
        super(cause);
    }

    protected ServiceExportException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
