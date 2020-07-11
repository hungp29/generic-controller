package org.example.genericcontroller.exception.generic;

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
