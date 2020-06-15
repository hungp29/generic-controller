package org.example.genericcontroller.support.generic.utils;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.exception.generic.ConstructorInvalidException;
import org.example.genericcontroller.exception.generic.FieldInvalidException;
import org.example.genericcontroller.support.generic.MappingClass;
import org.example.genericcontroller.support.generic.MappingField;
import org.example.genericcontroller.utils.ObjectUtils;
import org.example.genericcontroller.utils.constant.Constants;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
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
        validateThrow(dtoType, new ConstructorInvalidException("Data Transfer Object configuration is invalid"));
        List<String> fieldPaths = new ArrayList<>();
        if (null != dtoType) {
            List<Field> fields = ObjectUtils.getFields(dtoType, true);
            for (Field field : fields) {
                fieldPaths.addAll(getEntityMappingFieldPaths(field, lookingInner));
            }
        }
        return fieldPaths.stream().distinct().collect(Collectors.toList());
    }

    /**
     * Get key value of entity.
     *
     * @param dtoType Data Transfer Object type
     * @param record  Data as Map
     * @return key of entity
     */
    public static String getKey(Class<?> dtoType, Map<String, Object> record) {
        StringBuilder finalKey = new StringBuilder(Constants.EMPTY_STRING);
        List<String> keys = EntityUtils.getPrimaryKey(getEntityType(dtoType));
        for (String key : keys) {
            Object value = record.get(key);
            finalKey.append(null != value ? value.toString() : Constants.EMPTY_STRING)
                    .append(Constants.UNDERSCORE);
        }
        return finalKey.deleteCharAt(finalKey.length() - 1).toString();
    }

    /**
     * Convert list map record to list object Data Transfer Object.
     *
     * @param records list map record
     * @param dtoType Data Transfer Object type
     * @return list Data Transfer Object
     */
    public static Collection<Object> convertToListDataTransferObject(List<Map<String, Object>> records, Class<?> dtoType) {
        Map<String, Object> mapDTO = new HashMap<>();
        if (!CollectionUtils.isEmpty(records) && null != dtoType) {
            for (Map<String, Object> record : records) {
                String key = getKey(dtoType, record);
                Object dto = mapDTO.get(key);
                dto = convertToDataTransferObject(dto, Constants.EMPTY_STRING, record, dtoType);
                mapDTO.put(key, dto);
            }
        }
        return mapDTO.values();
    }

    /**
     * Convert data in map to Data Transfer Object.
     *
     * @param dto     instance of Data Transfer Object
     * @param prefix  the prefix to get value from map data
     * @param record  map data of record
     * @param dtoType Data Transfer Object type
     * @return Data Transfer Object
     */
    public static Object convertToDataTransferObject(Object dto, String prefix, Map<String, Object> record, Class<?> dtoType) {
        if (null != record && null != dtoType) {
            if (null == dto) {
                dto = newDataTransferObjectInstance(dtoType);
            }
            List<Field> dtoFields = ObjectUtils.getFields(dtoType, true);
            for (Field dtoField : dtoFields) {
                try {
                    Object value = convertToFieldOfDataTransferObject(prefix, record, dtoField);
                    if (ObjectUtils.fieldIsCollection(dtoField)) {
                        Collection collection = ObjectUtils.getValueOfField(dto, dtoField.getName(), Collection.class);
                        if (null == collection) {
                            collection = ObjectUtils.newInstanceCollection(dtoField.getType());
                        }
                        collection.add(value);
                        ObjectUtils.setValueForField(dto, dtoField.getName(), collection, true);
                    } else {
                        ObjectUtils.setValueForField(dto, dtoField.getName(), value, true);
                    }
                } catch (NoSuchMethodException e) {
                    throw new ConstructorInvalidException("Cannot new collection instance for field "
                            + dtoType.getSimpleName() + "." + dtoField.getName(), e);
                } catch (IllegalAccessException e) {
                    throw new FieldInvalidException("Cannot set/get value for field "
                            + dtoType.getSimpleName() + "." + dtoField.getName(), e);
                }
            }
        }
        return dto;
    }

    /**
     * Get value of field of Data Transfer Object.
     *
     * @param prefix the prefix to get data from map
     * @param record map data of record
     * @param field  field of Data Transfer Object
     * @return value of field
     */
    public static Object convertToFieldOfDataTransferObject(String prefix, Map<String, Object> record, Field field) {
        if (null != record && null != field) {
            String entityFieldPath = getEntityMappingFieldPath(field);
            if (!StringUtils.isEmpty(prefix)) {
                entityFieldPath = prefix + Constants.DOT + entityFieldPath;
            }

            Class<?> fieldType = ObjectUtils.getFieldType(field);
            if (validate(fieldType)) {
                return convertToDataTransferObject(null, entityFieldPath, record, fieldType);
            } else {
                return record.get(entityFieldPath);
            }
        }
        return null;
    }

    /**
     * New instance of Data Transfer Object.
     *
     * @param dtoType Data Transfer Object type
     * @return new instance Data Transfer Object
     */
    public static Object newDataTransferObjectInstance(Class<?> dtoType) {
        try {
            return ObjectUtils.newInstance(dtoType);
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new ConstructorInvalidException("Cannot found default constructor for " + dtoType.getSimpleName(), e);
        }
    }
}
