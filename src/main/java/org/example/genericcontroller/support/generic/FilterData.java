package org.example.genericcontroller.support.generic;

import lombok.Data;
import org.example.genericcontroller.support.generic.obj.DTOObject;

import java.util.List;
import java.util.Map;

/**
 * Search Extractor.
 *
 * @author hungp
 */
@Data
public class FilterData {

    private DTOObject dtoObject;
    private String[] filter;
    private Map<String, String> params;

    /**
     * Purpose for app new instance in controller.
     */
    public FilterData() {
    }

    public FilterData(Class<?> dtoType, String[] filter, Map<String, String> params) {
        this.dtoObject = DTOObject.of(dtoType);
        this.filter = filter;
        this.params = params;
    }

    public List<String> getMappingEntityFieldPath(boolean filter, boolean lookingInner, boolean includeCollection) {
        List<String> paths = dtoObject.getMappingFieldPath(false, false);
        List<String> paths2 = dtoObject.getMappingFieldPath(true, false);
        List<String> paths3 = dtoObject.getMappingFieldPath(false, true);
        List<String> paths4 = dtoObject.getMappingFieldPath(true, true);
//        List<String> lstMappingEntityField = extractor.getMappingEntityFieldPath(lookingInner, includeCollection);
//        if (filter && null != this.filter && this.filter.length > 0) {
//            List<String> keys = extractor.getMappingEntityPrimaryFieldPath(false);
//            List<String> keys2 = extractor.getMappingEntityPrimaryFieldPath(true);
//            System.out.println("AD");
//        }
//        return lstMappingEntityField;
        return null;
    }


}
