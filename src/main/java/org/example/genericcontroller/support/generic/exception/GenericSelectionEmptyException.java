package org.example.genericcontroller.support.generic.exception;

public class GenericSelectionEmptyException extends GenericException {

    public GenericSelectionEmptyException(String message) {
        super(message);
    }

    public GenericSelectionEmptyException(String message, Throwable thr) {
        super(message, thr);
    }
}
