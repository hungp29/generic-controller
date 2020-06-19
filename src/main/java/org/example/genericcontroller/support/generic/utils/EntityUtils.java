package org.example.genericcontroller.support.generic.utils;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.utils.ObjectUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
    public static void validateThrow(Class<? extends Audit> entityType, RuntimeException thr) {
        if (!validate(entityType)) {
            throw thr;
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

    /**
     * Checking field is foreign key or not.
     *
     * @param field the field need to check
     * @return true if field is foreign key
     */
    public static boolean isForeignKey(Field field) {
        return ObjectUtils.hasAnnotation(field, OneToOne.class) ||
                ObjectUtils.hasAnnotation(field, OneToMany.class) ||
                ObjectUtils.hasAnnotation(field, ManyToOne.class) ||
                ObjectUtils.hasAnnotation(field, ManyToMany.class);
    }

}
