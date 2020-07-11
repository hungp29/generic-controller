package org.example.genericcontroller.exception.generic;

public class GenericDuplicateException extends GenericException {

    public GenericDuplicateException(String message) {
        super(message);
    }

    public GenericDuplicateException(String message, Throwable thr) {
        super(message, thr);
    }
}
