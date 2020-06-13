package org.example.genericcontroller.exception.generic;

/**
 * Data Transfer Object Invalid Exception.
 *
 * @author hungp
 */
public class DataTransferObjectInvalidException extends RuntimeException {

    public DataTransferObjectInvalidException(String message) {
        super(message);
    }

    public DataTransferObjectInvalidException(String message, Throwable thr) {
        super(message, thr);
    }
}
