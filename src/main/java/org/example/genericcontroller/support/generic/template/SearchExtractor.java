package org.example.genericcontroller.support.generic.template;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Search Extractor.
 *
 * @author hungp
 */
@Data
public class SearchExtractor {

    private DTOExtractor dtoExtractor;
    private String[] filter;
    private Map<String, String> params;

    /**
     * Purpose for app new instance in controller.
     */
    public SearchExtractor() {
    }

    public SearchExtractor(Class<?> dtoType, String[] filter, Map<String, String> params) {
        dtoExtractor = DTOExtractor.of(dtoType);
        this.filter = filter;
        this.params = params;
    }

    public List<String> getEntityMappingFieldPath(boolean includeCollection) {
        return dtoExtractor.getMappingFieldPath(true, includeCollection);
    }
}
