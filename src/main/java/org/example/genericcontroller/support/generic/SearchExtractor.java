package org.example.genericcontroller.support.generic;

import org.example.genericcontroller.exception.generic.ConfigurationInvalidException;
import org.example.genericcontroller.support.generic.dtotemplate.DTOTemplate;

import java.util.Map;

/**
 * Search Extractor.
 *
 * @author hungp
 */
public class SearchExtractor {

    private Class<?> dtoType;
    private String[] filter;
    private Map<String, String> params;

    public Class<?> getDtoType() {
        return dtoType;
    }

    public void setDtoType(Class<?> dtoType) {
        if (!DTOTemplate.class.isAssignableFrom(dtoType)) {
            throw new ConfigurationInvalidException(dtoType.getName() + " don't extended " + DTOTemplate.class.getName());
        }
        this.dtoType = dtoType;
    }

    public String[] getFilter() {
        return filter;
    }

    public void setFilter(String[] filter) {
        this.filter = filter;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
