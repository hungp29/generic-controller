package org.example.genericcontroller.support.generic.exception;

public class GenericDuplicateException extends GenericException {

    public GenericDuplicateException(String message) {
        super(message);
    }

    public GenericDuplicateException(String message, Throwable thr) {
        super(message, thr);
    }
}
