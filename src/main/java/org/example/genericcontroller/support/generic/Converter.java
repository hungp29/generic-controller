package org.example.genericcontroller.support.generic;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.utils.ObjectUtils;
import org.example.genericcontroller.utils.constant.Constants;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Converter {

    private Converter() {
    }

    public static Class<? extends Audit> getEntityTypeFromDTO(Class<?> dtoType) {
        if (null != dtoType && ObjectUtils.hasAnnotation(dtoType, MappingClass.class)) {
            return ObjectUtils.getAnnotation(dtoType, MappingClass.class).value();
        }
        return null;
    }

    public static List<String> getFieldNames(Class<?> classType) {
        if (null != classType) {
            List<Field> fields = ObjectUtils.getFields(classType);
            if (!CollectionUtils.isEmpty(fields)) {
                return fields.stream().map(Field::getName).collect(Collectors.toList());
            }
        }
        return null;
    }

    public static Field getEntityFieldByDTOField(Field dtoField, Class<?> entityClass) {
        if (null != dtoField && null != entityClass) {
            MappingField mappingField = ObjectUtils.getAnnotation(dtoField, MappingField.class);
            String entityFieldName = dtoField.getName();
            if (null != mappingField && !StringUtils.isEmpty(mappingField.entityField())) {
                entityFieldName = mappingField.entityField();
            }

            Field entityField = ObjectUtils.getField(entityClass, entityFieldName);
        }
        return null;
    }

    public static boolean isForeignKeyField(Field field) {
        return null != ObjectUtils.getAnnotation(field, OneToOne.class) ||
                null != ObjectUtils.getAnnotation(field, OneToMany.class) ||
                null != ObjectUtils.getAnnotation(field, ManyToOne.class) ||
                null != ObjectUtils.getAnnotation(field, ManyToMany.class);
    }

    public static String getEntityFieldNameByDTOField(Field field) {
        if (null != field) {
            String entityFieldName = field.getName();
            MappingField mappingField = ObjectUtils.getAnnotation(field, MappingField.class);
            if (null != mappingField && !StringUtils.isEmpty(mappingField.entityField())) {
                entityFieldName = mappingField.entityField();
            }
            return entityFieldName;
        }
        return null;
    }

    public static List<String> getEntityFieldPath(Class<?> entityType, Field dtoField) {
        List<String> lstEntityFieldPath = new ArrayList<>();
        if (null != entityType && null != dtoField) {
            // Get entity field name. Ex: name or user.name
            String entityFieldName = getEntityFieldNameByDTOField(dtoField);
            String[] entityFieldPaths = entityFieldName.split(Constants.DOT_REGEX);

            Field subField = ObjectUtils.getField(entityType, entityFieldPaths[0]);

            if (entityFieldPaths.length > 1) {
                String nextPath = entityFieldName.substring(entityFieldName.indexOf(Constants.DOT) + 1);
            } else if (isForeignKeyField(subField)) {
//                List<String> subFieldPaths = getEntityFieldPath(subField.getType());
            } else {
                lstEntityFieldPath.add(entityFieldName);
            }
        }
        return lstEntityFieldPath;
    }

    public static List<String> getEntityFieldPath(Class<?> dtoType) {
        List<String> lstEntityFieldPath = new ArrayList<>();
        Class<? extends Audit> entityType = getEntityTypeFromDTO(dtoType);
        if (null != entityType) {
            List<Field> dtoFields = ObjectUtils.getFields(dtoType);
            for (Field dtoField : dtoFields) {
                lstEntityFieldPath.addAll(getEntityFieldPath(entityType, dtoField));
            }
        }
        return lstEntityFieldPath;
    }
}
