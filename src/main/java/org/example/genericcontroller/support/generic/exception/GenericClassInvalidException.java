package org.example.genericcontroller.support.generic.exception;

public class GenericClassInvalidException extends GenericException {

    public GenericClassInvalidException(String message) {
        super(message);
    }

    public GenericClassInvalidException(String message, Throwable thr) {
        super(message, thr);
    }
}
