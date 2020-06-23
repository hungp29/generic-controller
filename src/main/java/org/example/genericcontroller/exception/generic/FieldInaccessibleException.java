package org.example.genericcontroller.exception.generic;

/**
 * Field Inaccessible Exception.
 *
 * @author hungp
 */
public class FieldInaccessibleException extends GenericException {

    public FieldInaccessibleException(String message) {
        super(message);
    }

    public FieldInaccessibleException(String message, Throwable thr) {
        super(message, thr);
    }
}
