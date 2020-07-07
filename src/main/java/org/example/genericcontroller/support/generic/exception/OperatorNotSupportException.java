package org.example.genericcontroller.support.generic.exception;

/**
 * Operator not support exception.
 */
public class OperatorNotSupportException extends GenericException {

    public OperatorNotSupportException(String message) {
        super(message);
    }

    public OperatorNotSupportException(String message, Throwable thr) {
        super(message, thr);
    }
}
