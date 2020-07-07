package org.example.genericcontroller.support.generic.exception;

/**
 * Generic Exception.
 */
public class GenericException extends RuntimeException {

    public GenericException(String message) {
        super(message);
    }

    public GenericException(String message, Throwable thr) {
        super(message, thr);
    }
}
