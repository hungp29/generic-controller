package org.example.genericcontroller.support.generic;

import lombok.Data;
import org.example.genericcontroller.support.generic.dto.Extractor;

import java.util.List;
import java.util.Map;

/**
 * Search Extractor.
 *
 * @author hungp
 */
@Data
public class FilterData {

    private Extractor extractor;
    private String[] filter;
    private Map<String, String> params;

    /**
     * Purpose for app new instance in controller.
     */
    public FilterData() {
    }

    public FilterData(Class<?> dtoType, String[] filter, Map<String, String> params) {
        extractor = Extractor.of(dtoType);
        this.filter = filter;
        this.params = params;
    }

    public List<String> getEntityMappingFieldPath(boolean filter, boolean includeCollection) {
        return extractor.getMappingFieldPath(true, includeCollection);
    }
}
