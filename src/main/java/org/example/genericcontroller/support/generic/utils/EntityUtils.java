package org.example.genericcontroller.support.generic.utils;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.utils.ObjectUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity Utils.
 *
 * @author hungp
 */
public class EntityUtils {

    /**
     * Prevent new instance.
     */
    private EntityUtils() {
    }

    /**
     * Validate entity type has Entity annotation.
     *
     * @param entityType entity type
     * @return true if entity class has Entity annotation, otherwise return false
     */
    public static boolean validate(Class<? extends Audit> entityType) {
        return ObjectUtils.hasAnnotation(entityType, Entity.class);
    }

    /**
     * Validate entity type. If entity class don't has Entity annotation then throw Runtime Exception.
     *
     * @param entityType entity type
     * @param thr        Exception to throw if entity type is invalid
     */
    public static void validateThrow(Class<? extends Audit> entityType, Throwable thr) {
        if (!validate(entityType)) {
            throw new RuntimeException(thr);
        }
    }

    /**
     * Get Primary Key of entity.
     *
     * @param entityType entity class
     * @return the key of entity
     */
    public static List<String> getPrimaryKey(Class<? extends Audit> entityType) {
        List<String> keys = new ArrayList<>();
        if (null != entityType) {
            List<Field> fields = ObjectUtils.getFields(entityType, true);
            for (Field field : fields) {
                if (ObjectUtils.hasAnnotation(field, Id.class)) {
                    keys.add(field.getName());
                }
//                else if (ObjectUtils.hasAnnotation(field, EmbeddedId.class)) {
//                    List<Field> embeddedIdFields = ObjectUtils.getFields(field.getType());
//
//                }
            }
        }
        return keys;
    }


}
