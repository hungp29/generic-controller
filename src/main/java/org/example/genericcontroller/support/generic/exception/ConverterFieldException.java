package org.example.genericcontroller.support.generic.exception;

/**
 * Converter Field Exception.
 */
public class ConverterFieldException extends GenericException {

    public ConverterFieldException(String message) {
        super(message);
    }

    public ConverterFieldException(String message, Throwable thr) {
        super(message, thr);
    }
}
