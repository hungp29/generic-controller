package org.example.genericcontroller.exception.generic;

public class GenericException extends RuntimeException {

    public GenericException(String message) {
        super(message);
    }

    public GenericException(String message, Throwable thr) {
        super(message, thr);
    }
}
