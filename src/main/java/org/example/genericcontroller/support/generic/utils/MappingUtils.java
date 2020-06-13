package org.example.genericcontroller.support.generic.utils;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.exception.generic.EntityInvalidException;
import org.example.genericcontroller.support.generic.MappingClass;
import org.example.genericcontroller.utils.ObjectUtils;

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

    public static List<String> getMappingEntityFields(Class<?> dtoType) {
        List<String> entityFields = new ArrayList<>();
        if (ObjectUtils.hasAnnotation(dtoType, MappingClass.class)) {
            Class<? extends Audit> entityType = ObjectUtils.getAnnotation(dtoType, MappingClass.class).value();
            EntityUtils.validateThrow(entityType, new EntityInvalidException("Entity configuration is invalid"));

            List<String> fieldPaths = DataTransferObjectUtils.getEntityMappingFieldPaths(dtoType, true);

            fieldPaths.forEach(System.out::println);
        }
        return entityFields;
    }
}
