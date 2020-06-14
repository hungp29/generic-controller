package org.example.genericcontroller.support.generic.utils;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.exception.generic.DataTransferObjectInvalidException;
import org.example.genericcontroller.support.generic.MappingClass;
import org.example.genericcontroller.support.generic.MappingField;
import org.example.genericcontroller.utils.ObjectUtils;
import org.example.genericcontroller.utils.constant.Constants;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Data Transfer Object entity.
 *
 * @author hungp
 */
public class DataTransferObjectUtils {

    /**
     * Prevent new instance.
     */
    private DataTransferObjectUtils() {
    }

    /**
     * Validate DTO type has MappingClass annotation.
     *
     * @param dtoType DTO type
     * @return true if DTO class has MappingClass annotation, otherwise return false
     */
    public static boolean validate(Class<?> dtoType) {
        return ObjectUtils.hasAnnotation(dtoType, MappingClass.class);
    }

    /**
     * Validate DTO type. If DTO class don't has MappingClass annotation then throw Runtime Exception.
     *
     * @param dtoType DTO type
     * @param thr     Exception to throw if DTO class is invalid
     */
    public static void validateThrow(Class<?> dtoType, Throwable thr) {
        if (!validate(dtoType)) {
            throw new RuntimeException(thr);
        }
    }

    /**
     * Get entity type is configuration of Data Transfer Object.
     *
     * @param dtoType Data Transfer Object type
     * @return entity type
     */
    public static Class<? extends Audit> getEntityType(Class<?> dtoType) {
        if (null != dtoType && ObjectUtils.hasAnnotation(dtoType, MappingClass.class)) {
            return ObjectUtils.getAnnotation(dtoType, MappingClass.class).value();
        }
        return null;
    }

    /**
     * Get mapping entity path by Data Transfer Object field. Don't looking inner object.
     *
     * @param field Data Transfer Object field
     * @return mapping entity path of field
     */
    public static String getEntityMappingFieldPath(Field field) {
        List<String> fieldPaths = getEntityMappingFieldPaths(field, false);
        if (fieldPaths.size() > 0) {
            return fieldPaths.get(0);
        }
        return null;
    }

    /**
     * Get mapping entity path by Data Transfer Object field.
     *
     * @param field        Data Transfer Object field
     * @param lookingInner flag to looking inner object
     * @return list mapping entity path of field
     */
    public static List<String> getEntityMappingFieldPaths(Field field, boolean lookingInner) {
        List<String> fieldPaths = new ArrayList<>();
        if (null != field) {
            String fieldPath = field.getName();
            MappingField mappingField = ObjectUtils.getAnnotation(field, MappingField.class);
            if (null != mappingField && !StringUtils.isEmpty(mappingField.entityField())) {
                fieldPath = mappingField.entityField();
            }

            Class<?> innerClass = ObjectUtils.getFieldType(field);
            if (lookingInner && validate(innerClass)) {
                List<String> innerFieldPaths = getEntityMappingFieldPaths(innerClass, true);
                if (!CollectionUtils.isEmpty(innerFieldPaths)) {
                    String finalFieldPath = fieldPath.concat(Constants.DOT);
                    fieldPaths.addAll(innerFieldPaths.stream().map(finalFieldPath::concat).collect(Collectors.toList()));
                }
            } else {
                fieldPaths.add(fieldPath);
            }
        }
        return fieldPaths.stream().distinct().collect(Collectors.toList());
    }

    /**
     * Get mapping entity path by Data Transfer Object type. Don't looking inner object.
     *
     * @param dtoType Data Transfer Object type
     * @return list mapping entity path of object
     */
    public static List<String> getEntityMappingFieldPaths(Class<?> dtoType) {
        return getEntityMappingFieldPaths(dtoType, false);
    }

    /**
     * Get mapping entity path by Data Transfer Object type.
     *
     * @param dtoType      Data Transfer Object type
     * @param lookingInner flag to looking inner object
     * @return list mapping entity path of object
     */
    public static List<String> getEntityMappingFieldPaths(Class<?> dtoType, boolean lookingInner) {
        validateThrow(dtoType, new DataTransferObjectInvalidException("Data Transfer Object configuration is invalid"));
        List<String> fieldPaths = new ArrayList<>();
        if (null != dtoType) {
            List<Field> fields = ObjectUtils.getFields(dtoType, true);
            for (Field field : fields) {
                fieldPaths.addAll(getEntityMappingFieldPaths(field, lookingInner));
            }
        }
        return fieldPaths.stream().distinct().collect(Collectors.toList());
    }

    public static String getKey(String prefix, Class<?> dtoType, Map<String, Object> record) {
        StringBuilder finalKey = new StringBuilder(Constants.EMPTY_STRING);
        List<String> keys = EntityUtils.getPrimaryKey(getEntityType(dtoType));
        for (String key : keys) {
            String fieldPath = StringUtils.isEmpty(prefix) ? key : prefix + Constants.DOT + key;
            Object value = record.get(fieldPath);
            finalKey.append(null != value ? value.toString() : Constants.EMPTY_STRING)
                    .append(Constants.UNDERSCORE);
        }
        return finalKey.deleteCharAt(finalKey.length() - 1).toString();
    }

    public static Object convertDataToFieldDataTransferObject(String prefix, Field field, Map<String, Object> record) {
        Class<?> innerClass = ObjectUtils.getFieldType(field);
        if (validate(innerClass)) {

        }
        String fieldPath = getEntityMappingFieldPath(field);
        if (!StringUtils.isEmpty(prefix)) {
            fieldPath = prefix + Constants.DOT + fieldPath;
        }

        return null;
    }

    public static Map<String, Object> convertDataToDataToDataTransferObject(Map<String, Object> mapDTO, String prefix,
                                                                            Map<String, Object> record, Class<?> dtoType)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        if (null == mapDTO) {
            mapDTO = new HashMap<>();
        }
        String key = getKey(prefix, dtoType, record);
        Object dto = mapDTO.get(key);
        if (null == dto) {
            dto = ObjectUtils.newInstance(dtoType);
        }

        List<Field> dtoFields = ObjectUtils.getFields(dtoType);
        for (Field dtoField : dtoFields) {

        }

        mapDTO.put(key, dto);
        return mapDTO;
    }
}
