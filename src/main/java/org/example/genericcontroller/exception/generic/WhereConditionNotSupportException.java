package org.example.genericcontroller.exception.generic;

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
