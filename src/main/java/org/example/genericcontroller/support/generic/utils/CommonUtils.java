package org.example.genericcontroller.support.generic.utils;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Common Utils.
 *
 * @author hungp
 */
public class CommonUtils {

    /**
     * Count length of array of field from Map Data.
     *
     * @param prefix  prefix field path
     * @param mapData map field and data of entity
     * @return length of array of field
     */
    public static int countLengthOfArray(String prefix, Map<String, Object> mapData) {
        int length = 0;
        if (!StringUtils.isEmpty(prefix) && !CollectionUtils.isEmpty(mapData)) {
            for (String key : mapData.keySet()) {
                Matcher matcher = Pattern.compile("^" + prefix + "\\[(\\d+)\\](.*)").matcher(key);
                if (matcher.matches() && matcher.groupCount() > 1) {
                    length = Math.max(length, Integer.parseInt(matcher.group(1)) + 1);
                }
            }
        }
        return length;
    }
}
