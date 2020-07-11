package org.example.genericcontroller.exception.generic;

/**
 * Argument Exception.
 */
public class ArgumentException extends GenericException {

    public ArgumentException(String message) {
        super(message);
    }

    public ArgumentException(String message, Throwable thr) {
        super(message, thr);
    }
}
