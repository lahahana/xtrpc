package com.github.lahahana.xtrpc.common.exception;

/**
 * Used when service is not available when call remote method
 * */
public class ServiceNotAvailableException extends RuntimeException {

    public ServiceNotAvailableException() {
    }

    public ServiceNotAvailableException(String message) {
        super(message);
    }

    public ServiceNotAvailableException(Throwable cause) {
        super(cause);
    }

    public ServiceNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
