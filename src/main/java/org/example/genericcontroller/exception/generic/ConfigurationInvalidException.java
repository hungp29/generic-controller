package org.example.genericcontroller.exception.generic;

/**
 * Configuration Invalid Exception.
 *
 * @author hungp
 */
public class ConfigurationInvalidException extends GenericException {

    public ConfigurationInvalidException(String message) {
        super(message);
    }

    public ConfigurationInvalidException(String message, Throwable thr) {
        super(message, thr);
    }
}
