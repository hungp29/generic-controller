package org.example.genericcontroller.exception.generic;

/**
 * Entity Invalid Exception.
 *
 * @author hungp
 */
public class EntityInvalidException extends RuntimeException {

    public EntityInvalidException(String message) {
        super(message);
    }

    public EntityInvalidException(String message, Throwable thr) {
        super(message, thr);
    }
}
