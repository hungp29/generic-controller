package org.example.genericcontroller.support.generic.exception;

/**
 * Param Invalid Exception.
 */
public class ParamInvalidException extends GenericException {

    public ParamInvalidException(String message) {
        super(message);
    }

    public ParamInvalidException(String message, Throwable thr) {
        super(message, thr);
    }
}
