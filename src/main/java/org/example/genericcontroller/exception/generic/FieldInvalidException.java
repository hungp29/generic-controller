package org.example.genericcontroller.exception.generic;

/**
 * Field Invalid Exception.
 *
 * @author hungp
 */
public class FieldInvalidException extends RuntimeException {

    public FieldInvalidException(String message) {
        super(message);
    }

    public FieldInvalidException(String message, Throwable thr) {
        super(message, thr);
    }
}
