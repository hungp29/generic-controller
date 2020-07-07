package org.example.genericcontroller.support.generic.exception;

/**
 * Condition Value Invalid Exception.
 */
public class ConditionValueInvalidException extends GenericException {

    public ConditionValueInvalidException(String message) {
        super(message);
    }

    public ConditionValueInvalidException(String message, Throwable thr) {
        super(message, thr);
    }
}
