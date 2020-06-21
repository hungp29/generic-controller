package org.example.genericcontroller.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Date Time Utils.
 *
 * @author hungp
 */
public class DateTimeUtils {

    /**
     * Prevent new instance.
     */
    private DateTimeUtils() {
    }

    /**
     * Convert LocalDateTime to milliseconds.
     *
     * @param dateTime date time value
     * @return milliseconds
     */
    public static long convertLocalDateTimeToMilli(LocalDateTime dateTime) {
        if (null != dateTime) {
            return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
        return 0L;
    }

    /**
     * Convert milliseconds to LocalDateTime.
     *
     * @param millis milliseconds
     * @return LocalDateTime instance
     */
    public static LocalDateTime convertMilliToLocalDateTime(long millis) {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

}
