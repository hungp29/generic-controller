package org.example.genericcontroller.exception.generic;

public class GenericSelectionEmptyException extends RuntimeException {

    public GenericSelectionEmptyException(String message) {
        super(message);
    }

    public GenericSelectionEmptyException(String message, Throwable thr) {
        super(message, thr);
    }
}
