package org.example.genericcontroller.exception.generic;

public class GenericClassInvalidException extends GenericException {

    public GenericClassInvalidException(String message) {
        super(message);
    }

    public GenericClassInvalidException(String message, Throwable thr) {
        super(message, thr);
    }
}
