package org.example.genericcontroller.support.generic.utils;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.utils.ObjectUtils;
import org.example.genericcontroller.utils.constant.Constants;
import org.springframework.util.StringUtils;

import javax.persistence.*;
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
    public static boolean validate(Class<?> entityType) {
        return ObjectUtils.hasAnnotation(entityType, Entity.class);
    }

    /**
     * Validate entity type. If entity class don't has Entity annotation then throw Runtime Exception.
     *
     * @param entityType entity type
     * @param thr        Exception to throw if entity type is invalid
     */
    public static void validateThrow(Class<?> entityType, RuntimeException thr) {
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

    /**
     * Checking field is primary or not.
     *
     * @param entityType Entity type
     * @param fieldPath  field path
     * @return true if field is primary key
     */
    public static boolean isPrimaryKey(Class<?> entityType, String fieldPath) {
        boolean isPrimaryKey = false;
        if (null != entityType && !StringUtils.isEmpty(fieldPath)) {
            String[] paths = fieldPath.split(Constants.DOT_REGEX);

            Field field = ObjectUtils.getField(entityType, paths[0], true);
            Class<?> innerClass = MappingUtils.getFieldType(field);
            if (paths.length > 1 && validate(innerClass)) {
                String nextPath = fieldPath.substring(fieldPath.indexOf(Constants.DOT) + 1);
                isPrimaryKey = isPrimaryKey(innerClass, nextPath);
            } else {
                isPrimaryKey = ObjectUtils.hasAnnotation(field, Id.class);
            }
        }
        return isPrimaryKey;
    }

    /**
     * Get entity field by path.
     *
     * @param entityType Entity type
     * @param fieldPath  field path
     * @return field
     */
    public static Field getFieldByPath(Class<?> entityType, String fieldPath) {
        Field field = null;
        if (null != entityType && !StringUtils.isEmpty(fieldPath)) {
            String[] paths = fieldPath.split(Constants.DOT_REGEX);

            field = ObjectUtils.getField(entityType, paths[0], true);
            Class<?> innerClass = MappingUtils.getFieldType(field);
            if (paths.length > 1 && validate(innerClass)) {
                String nextPath = fieldPath.substring(fieldPath.indexOf(Constants.DOT) + 1);
                field = getFieldByPath(innerClass, nextPath);
            }
        }
        return field;
    }

}
