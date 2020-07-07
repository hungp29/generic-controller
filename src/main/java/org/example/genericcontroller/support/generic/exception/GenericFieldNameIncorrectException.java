package org.example.genericcontroller.support.generic.exception;

/**
 * Generic Field Name Incorrect Exception.
 */
public class GenericFieldNameIncorrectException extends GenericException {

    public GenericFieldNameIncorrectException(String message) {
        super(message);
    }

    public GenericFieldNameIncorrectException(String message, Throwable thr) {
        super(message, thr);
    }
}
