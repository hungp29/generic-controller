package org.example.genericcontroller.support.generic.exception;

/**
 * Where Condition Not Support Exception.
 *
 * @author hungp
 */
public class WhereConditionNotSupportException extends GenericException {

    public WhereConditionNotSupportException(String message) {
        super(message);
    }

    public WhereConditionNotSupportException(String message, Throwable thr) {
        super(message, thr);
    }
}
