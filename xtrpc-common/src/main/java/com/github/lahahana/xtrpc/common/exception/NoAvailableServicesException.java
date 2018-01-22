package com.github.lahahana.xtrpc.common.exception;


/**
 * Be thrown when there is no available ref services of {@link com.github.lahahana.xtrpc.common.domain.Service}
 */
public class NoAvailableServicesException extends RuntimeException {

    public NoAvailableServicesException() {
        super();
    }
}
