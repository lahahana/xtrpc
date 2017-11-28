package com.github.lahahana.xtrpc.common.exception;

/**
 * Used when Service not found in service register center
 * */
public class ServiceNotFoundException extends RuntimeException {

    public ServiceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceNotFoundException(String message) {
        super(message);
    }
}

