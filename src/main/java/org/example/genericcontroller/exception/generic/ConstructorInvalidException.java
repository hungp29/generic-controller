package org.example.genericcontroller.exception.generic;

/**
 * Data Transfer Object Invalid Exception.
 *
 * @author hungp
 */
public class ConstructorInvalidException extends GenericException {

    public ConstructorInvalidException(String message) {
        super(message);
    }

    public ConstructorInvalidException(String message, Throwable thr) {
        super(message, thr);
    }
}
