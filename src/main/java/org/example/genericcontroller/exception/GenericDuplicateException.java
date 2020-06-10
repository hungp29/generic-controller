package org.example.genericcontroller.exception;

public class GenericDuplicateException extends RuntimeException {

    public GenericDuplicateException(String message) {
        super(message);
    }

    public GenericDuplicateException(String message, Throwable thr) {
        super(message, thr);
    }
}
