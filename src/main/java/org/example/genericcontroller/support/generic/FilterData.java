package org.example.genericcontroller.support.generic;

import lombok.Data;

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
}
