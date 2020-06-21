package org.example.genericcontroller.exception.generic;

/**
 * Operator Pattern Invalid Exception.
 */
public class OperatorPatternInvalidException extends RuntimeException {

    public OperatorPatternInvalidException(String message) {
        super(message);
    }

    public OperatorPatternInvalidException(String message, Throwable thr) {
        super(message, thr);
    }
}
