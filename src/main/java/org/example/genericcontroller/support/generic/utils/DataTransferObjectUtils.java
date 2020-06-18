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
    public static void validateThrow(Class<?> dtoType, RuntimeException thr) {
        if (!validate(dtoType)) {
            throw thr;
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
        List<String> fieldPaths = getEntityMappingFieldPaths(field, false, true);
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
    public static List<String> getEntityMappingFieldPaths(Field field, boolean lookingInner, boolean includeCollection) {
        List<String> fieldPaths = new ArrayList<>();
        if (null != field) {
            String fieldPath = field.getName();
            MappingField mappingField = ObjectUtils.getAnnotation(field, MappingField.class);
            if (null != mappingField && !StringUtils.isEmpty(mappingField.entityField())) {
                fieldPath = mappingField.entityField();
            }

            Class<?> innerClass = MappingUtils.getFieldType(field);
            boolean isCollection = ObjectUtils.fieldIsCollection(field);
            // Case 1: Field is normal field >> get one entity mapping field has been configuration in MappingField annotation
            // Case 2: Field is another DTO field, lookingInner = true, includeCollection = true >> looking all field of DTO field
            // Case 3: Field is another DTO field, lookingInner = true, includeCollection = false >> only non Collection field will be looking to get mapping fields
            if (lookingInner && validate(innerClass) && (includeCollection || !isCollection)) {
                List<String> innerFieldPaths = getEntityMappingFieldPaths(innerClass, true, true);
                if (!CollectionUtils.isEmpty(innerFieldPaths)) {
                    String finalFieldPath = fieldPath.concat(Constants.DOT);
                    fieldPaths.addAll(innerFieldPaths.stream().map(finalFieldPath::concat).collect(Collectors.toList()));
                }
            } else if (includeCollection || !isCollection) {
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
        return getEntityMappingFieldPaths(dtoType, false, true);
    }

    /**
     * Get mapping entity path by Data Transfer Object type.
     *
     * @param dtoType      Data Transfer Object type
     * @param lookingInner flag to looking inner object
     * @return list mapping entity path of object
     */
    public static List<String> getEntityMappingFieldPaths(Class<?> dtoType, boolean lookingInner, boolean includeCollection) {
        List<String> fieldPaths = new ArrayList<>();
        List<Field> fields = ObjectUtils.getFields(dtoType, true);
        for (Field field : fields) {
            fieldPaths.addAll(getEntityMappingFieldPaths(field, lookingInner, includeCollection));
        }
        return fieldPaths.stream().distinct().collect(Collectors.toList());
    }

    /**
     * Get primary key fields.
     *
     * @param dtoType           Data Transfer Object type
     * @param includeCollection flag to include collection field to list
     * @return list primary key
     */
    public static List<String> getEntityMappingFieldPathsPrimary(Class<?> dtoType, boolean includeCollection) {
        // Get primary key of Entity mapping with DTO
        List<String> entityKeyFields = EntityUtils.getPrimaryKey(getEntityType(dtoType));
        List<Field> fields = ObjectUtils.getFields(dtoType, true);
        // For each field of DTO, if field is another DTO then get also.
        for (Field field : fields) {
            Class<?> fieldType = MappingUtils.getFieldType(field);
            if (validate(fieldType) && (includeCollection || !ObjectUtils.fieldIsCollection(field))) {
                String fieldPath = getEntityMappingFieldPath(field);
                fieldPath = null != fieldPath ? fieldPath + Constants.DOT : Constants.EMPTY_STRING;
                List<String> innerKeyFieldPaths = getEntityMappingFieldPathsPrimary(fieldType, includeCollection)
                        .stream().map(fieldPath::concat).collect(Collectors.toList());
                entityKeyFields.addAll(innerKeyFieldPaths);
            }
        }
        return entityKeyFields;
    }

    /**
     * Get entity mapping of collection field.
     *
     * @param dtoType Data Transfer Object type
     * @return list field
     */
    public static List<String> getEntityMappingFieldPathsCollection(Class<?> dtoType) {
        return getEntityMappingFieldPathsCollection(dtoType, false);
    }

    /**
     * Get entity mapping of collection field (looking inner class).
     *
     * @param dtoType      ata Transfer Object type
     * @param lookingInner flag to specify looking inner class
     * @return list field
     */
    public static List<String> getEntityMappingFieldPathsCollection(Class<?> dtoType, boolean lookingInner) {
        List<String> fieldPaths = new ArrayList<>();
        List<Field> fields = ObjectUtils.getFields(dtoType, true);
        for (Field field : fields) {
            if (validate(MappingUtils.getFieldType(field)) && ObjectUtils.fieldIsCollection(field)) {
                fieldPaths.addAll(getEntityMappingFieldPaths(field, lookingInner, true));
            }
        }
        return fieldPaths;
    }

    /**
     * Get key value of entity (data from map).
     *
     * @param prefix  apply for inner object
     * @param dtoType Data Transfer Object
     * @param record  record data
     * @return value of keys
     */
    public static String getKey(String prefix, Class<?> dtoType, Map<String, Object> record) {
        StringBuilder finalKey = new StringBuilder(Constants.EMPTY_STRING);
        List<String> keys = EntityUtils.getPrimaryKey(getEntityType(dtoType));
        for (String key : keys) {
            if (!StringUtils.isEmpty(prefix)) {
                key = prefix + Constants.DOT + key;
            }
            Object value = record.get(key);
            finalKey.append(null != value ? value.toString() : Constants.EMPTY_STRING)
                    .append(Constants.UNDERSCORE);
        }
        return finalKey.deleteCharAt(finalKey.length() - 1).toString(); // remove last underscore
    }

    /**
     * Get key value of entity (data from object).
     *
     * @param dtoType Data Transfer Object type
     * @param record  object data
     * @return value of keys
     * @throws IllegalAccessException throw exception if cannot find field in object
     */
    public static String getKey(Class<?> dtoType, Object record) throws IllegalAccessException {
        StringBuilder finalKey = new StringBuilder(Constants.EMPTY_STRING);
        List<String> keys = EntityUtils.getPrimaryKey(getEntityType(dtoType));
        for (String key : keys) {
            Object value = ObjectUtils.getValueOfField(record, key);
            finalKey.append(null != value ? value.toString() : Constants.EMPTY_STRING)
                    .append(Constants.UNDERSCORE);
        }
        return finalKey.deleteCharAt(finalKey.length() - 1).toString(); // remove last underscore
    }

    /**
     * Check field has data or not.
     *
     * @param record    record data
     * @param prefix    prefix
     * @param fieldName field name
     * @return true if can found data of field in map record
     */
    private static boolean hasData(Map<String, Object> record, String prefix, String fieldName) {
        boolean hasData = false;
        if (!StringUtils.isEmpty(prefix)) {
            fieldName = prefix + Constants.DOT + fieldName;
        }
        for (String key : record.keySet()) {
            if (null != record.get(key) &&
                    (key.equals(fieldName) || key.startsWith(fieldName.concat(Constants.DOT)))) {
                hasData = true;
                break;
            }
        }

        return hasData;
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
    public static Object convertToDataTransferObject(Object dto, String prefix, Map<String, Object> record,
                                                     Class<?> dtoType, String[] filter) {
        if (null != record && null != dtoType) {
            if (null == dto) {
                dto = newDataTransferObjectInstance(dtoType);
            }
            List<Field> dtoFields = ObjectUtils.getFields(dtoType, true);
            for (Field dtoField : dtoFields) {
                try {
                    Object value = null;
                    if (hasData(record, prefix, getEntityMappingFieldPath(dtoField))) {
                        value = convertToFieldOfDataTransferObject(dto, prefix, record, dtoField, filter);
                    }
                    ObjectUtils.setValueForField(dto, dtoField.getName(), value, true);
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
    public static Object convertToFieldOfDataTransferObject(Object dto, String prefix, Map<String, Object> record, Field field, String[] filter) {
        Object reValue = null;
        if (null != record && null != field) {
            String entityFieldPath = getEntityMappingFieldPath(field);
            if (!StringUtils.isEmpty(prefix)) {
                entityFieldPath = prefix + Constants.DOT + entityFieldPath;
            }

            Class<?> fieldType = MappingUtils.getFieldType(field);
            if (validate(fieldType)) {
                Object innerDTO = null;
                try {
                    if (ObjectUtils.fieldIsCollection(field)) {
                        Collection collection = ObjectUtils.getValueOfField(dto, field.getName(), Collection.class);
                        if (null == collection) {
                            collection = ObjectUtils.newInstanceCollection(field.getType());
                        } else {
                            for (Object obj : collection) {
                                String keyOne = getKey(fieldType, obj);
                                String keyTwo = getKey(entityFieldPath, fieldType, record);
                                if (keyOne.equals(keyTwo)) {
                                    innerDTO = obj;
                                    break;
                                }
                            }
                        }

                        Object value = convertToDataTransferObject(innerDTO, entityFieldPath, record, fieldType, filter);

                        if (null == innerDTO) {
                            collection.add(value);
                        }
                        reValue = collection;
                    } else {
                        reValue = convertToDataTransferObject(null, entityFieldPath, record, fieldType, filter);
                    }
                } catch (NoSuchMethodException e) {
                    throw new ConstructorInvalidException("Cannot new collection instance for field "
                            + dto.getClass().getSimpleName() + "." + field.getName(), e);
                } catch (IllegalAccessException e) {
                    throw new FieldInvalidException("Cannot get value for field "
                            + dto.getClass().getSimpleName() + "." + field.getName(), e);
                }
            } else if (MappingUtils.isKeepField(entityFieldPath, filter)) {
                reValue = record.get(entityFieldPath);
            }
        }
        return reValue;
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
