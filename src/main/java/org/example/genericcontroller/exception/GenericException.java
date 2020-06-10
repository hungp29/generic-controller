package org.example.genericcontroller.exception;

public class GenericException extends RuntimeException {

    public GenericException(String message) {
        super(message);
    }

    public GenericException(String message, Throwable thr) {
        super(message, thr);
    }
}
