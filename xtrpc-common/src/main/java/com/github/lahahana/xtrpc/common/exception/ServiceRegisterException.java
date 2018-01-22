package com.github.lahahana.xtrpc.common.exception;

/**
 * Be thrown when service is unable to register to service registry
 */
public class ServiceRegisterException extends Exception {

    public ServiceRegisterException() {
        super();
    }

    public ServiceRegisterException(String message) {
        super(message);
    }

    public ServiceRegisterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceRegisterException(Throwable cause) {
        super(cause);
    }

    protected ServiceRegisterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
