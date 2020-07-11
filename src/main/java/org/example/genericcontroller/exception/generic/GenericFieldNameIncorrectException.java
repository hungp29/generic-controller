package org.example.genericcontroller.exception.generic;

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
