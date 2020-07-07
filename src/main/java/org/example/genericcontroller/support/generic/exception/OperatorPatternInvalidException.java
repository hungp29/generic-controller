package org.example.genericcontroller.support.generic.exception;

/**
 * Operator Pattern Invalid Exception.
 */
public class OperatorPatternInvalidException extends GenericException {

    public OperatorPatternInvalidException(String message) {
        super(message);
    }

    public OperatorPatternInvalidException(String message, Throwable thr) {
        super(message, thr);
    }
}
