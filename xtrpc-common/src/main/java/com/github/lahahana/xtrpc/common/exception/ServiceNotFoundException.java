package com.github.lahahana.xtrpc.common.exception;

/**
 * Be thrown when {@link com.github.lahahana.xtrpc.common.domain.Service} not found in remote {@link com.github.lahahana.xtrpc.server.registry.ServiceRegistry} cent
 * */
public class ServiceNotFoundException extends Exception {

    public ServiceNotFoundException() {
        super();
    }

    public ServiceNotFoundException(String message) {
        super(message);
    }

    public ServiceNotFoundException(Throwable cause) {
        super(cause);
    }

    public ServiceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

