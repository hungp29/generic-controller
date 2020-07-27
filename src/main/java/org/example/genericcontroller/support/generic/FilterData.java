package org.example.genericcontroller.support.generic;

import lombok.Data;
import org.example.genericcontroller.utils.constant.Constants;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * Search Extractor.
 *
 * @author hungp
 */
@Data
public class FilterData {

    private Class<?> dtoType;
    private String[] filter;
    private Map<String, String> params;

    /**
     * Purpose for app new instance in controller.
     */
    public FilterData() {
    }

    public FilterData(Class<?> dtoType, String[] filter, Map<String, String> params) {
        this.dtoType = dtoType;
        this.filter = filter;
        this.params = params;
    }

    public boolean isKeepField(String fieldPath) {
//        boolean keep = !StringUtils.isEmpty(fieldPath) && null == filter;
//        if (!keep && null != filter) {
//            for (String keepField : filter) {
//                if (fieldPath.equals(keepField) || fieldPath.startsWith(keepField.concat(Constants.DOT))) {
//                    keep = true;
//                    break;
//                }
//            }
//        }
//        return keep;
        return true;
    }
}
