package org.example.genericcontroller.exception.generic;

public class GenericSelectionEmptyException extends GenericException {

    public GenericSelectionEmptyException(String message) {
        super(message);
    }

    public GenericSelectionEmptyException(String message, Throwable thr) {
        super(message, thr);
    }
}
