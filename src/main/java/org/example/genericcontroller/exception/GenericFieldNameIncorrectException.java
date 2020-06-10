package org.example.genericcontroller.exception;

public class GenericFieldNameIncorrectException extends RuntimeException {

    public GenericFieldNameIncorrectException(String message) {
        super(message);
    }

    public GenericFieldNameIncorrectException(String message, Throwable thr) {
        super(message, thr);
    }
}
