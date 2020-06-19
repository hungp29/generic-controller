package org.example.genericcontroller.exception.generic;

/**
 * Converter Field Invalid exception.
 */
public class ConverterFieldInvalidException extends RuntimeException {

    public ConverterFieldInvalidException(String message) {
        super(message);
    }

    public ConverterFieldInvalidException(String message, Throwable thr) {
        super(message, thr);
    }
}
