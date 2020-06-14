package org.example.genericcontroller.support.generic.utils;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.exception.generic.EntityInvalidException;
import org.example.genericcontroller.support.generic.MappingClass;
import org.example.genericcontroller.utils.ObjectUtils;
import org.example.genericcontroller.utils.constant.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapping Utils.
 *
 * @author hungp
 */
public class MappingUtils {

    /**
     * Prevent new instance.
     */
    private MappingUtils() {
    }

    /**
     * Get mapping entity field paths after filter.
     *
     * @param dtoType Data Transfer Object type
     * @param filter  array filter field
     * @return list entity field path
     */
    public static List<String> getEntityMappingFieldPaths(Class<?> dtoType, String[] filter) {
        List<String> entityFieldPaths = new ArrayList<>();
        if (ObjectUtils.hasAnnotation(dtoType, MappingClass.class)) {
            Class<? extends Audit> entityType = ObjectUtils.getAnnotation(dtoType, MappingClass.class).value();
            EntityUtils.validateThrow(entityType, new EntityInvalidException("Entity configuration is invalid"));

            entityFieldPaths = filterFieldPath(DataTransferObjectUtils.getEntityMappingFieldPaths(dtoType, true), filter);
        }
        return entityFieldPaths;
    }

    /**
     * Filter field path.
     *
     * @param fieldPaths list field path
     * @param filter     list field should be keeping
     * @return list field path after filter
     */
    public static List<String> filterFieldPath(List<String> fieldPaths, String[] filter) {
        if (!ObjectUtils.isEmpty(filter)) {
            int index = 0;
            while (index < fieldPaths.size()) {
                String entityFieldPath = fieldPaths.get(index);

                boolean keep = false;
                for (String keepField : filter) {
                    if (entityFieldPath.equals(keepField) || entityFieldPath.startsWith(keepField.concat(Constants.DOT))) {
                        keep = true;
                        break;
                    }
                }

                if (keep) {
                    index++;
                } else {
                    fieldPaths.remove(index);
                }
            }
        }
        return fieldPaths;
    }
}
