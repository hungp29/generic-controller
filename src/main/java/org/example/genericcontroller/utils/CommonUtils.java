package org.example.genericcontroller.utils;

import org.example.genericcontroller.utils.constant.Constants;

/**
 * Common Utils.
 *
 * @author hungp
 */
public class CommonUtils {

    private CommonUtils() {
    }

    /**
     * Trim string value.
     *
     * @param value the value to trim
     * @return return the value after trim whitespace
     */
    public static String trim(String value) {
        return null != value ? value.trim() : Constants.EMPTY_STRING;
    }
}
