package org.example.genericcontroller.exception;

public class GenericClassInvalidException extends RuntimeException {

    public GenericClassInvalidException(String message) {
        super(message);
    }

    public GenericClassInvalidException(String message, Throwable thr) {
        super(message, thr);
    }
}
