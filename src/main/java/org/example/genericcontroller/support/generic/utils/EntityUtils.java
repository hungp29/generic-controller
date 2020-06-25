package org.example.genericcontroller.support.generic.utils;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.exception.generic.ConfigurationInvalidException;
import org.example.genericcontroller.exception.generic.ConstructorInvalidException;
import org.example.genericcontroller.exception.generic.FieldInaccessibleException;
import org.example.genericcontroller.support.generic.DataTransferObjectMapping;
import org.example.genericcontroller.utils.ObjectUtils;
import org.example.genericcontroller.utils.constant.Constants;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * Get Create Request DTO.
     *
     * @param entityType Entity type
     * @return Create Request DTO
     */
    public static Class<?> getCreateRequestDTO(Class<?> entityType) {
        validateThrow(entityType, new ConfigurationInvalidException(entityType.getName() + ": Entity configuration is invalid"));
        DataTransferObjectMapping dataTransferObjectMapping = ObjectUtils.getAnnotation(entityType, DataTransferObjectMapping.class);
        if (null != dataTransferObjectMapping) {
            return dataTransferObjectMapping.forCreateRequest();
        }
        return null;
    }

    /**
     * Get Read Response DTO.
     *
     * @param entityType Entity type
     * @return Create Request DTO
     */
    public static Class<?> getReadResponseDTO(Class<?> entityType) {
        validateThrow(entityType, new ConfigurationInvalidException(entityType.getName() + ": Entity configuration is invalid"));
        DataTransferObjectMapping dataTransferObjectMapping = ObjectUtils.getAnnotation(entityType, DataTransferObjectMapping.class);
        if (null != dataTransferObjectMapping) {
            return dataTransferObjectMapping.forRead();
        }
        return null;
    }

    /**
     * Count length of array of field from Map Data.
     *
     * @param prefix  prefix field path
     * @param mapData map field and data of entity
     * @return length of array of field
     */
    private static int countLengthOfArray(String prefix, Map<String, Object> mapData) {
        int length = 0;
        if (!StringUtils.isEmpty(prefix) && !CollectionUtils.isEmpty(mapData)) {
            for (String key : mapData.keySet()) {
                Matcher matcher = Pattern.compile("^" + prefix + "\\[(\\d+)\\](.*)").matcher(key);
                if (matcher.matches() && matcher.groupCount() > 1) {
                    length = Math.max(length, Integer.parseInt(matcher.group(1)) + 1);
                }
            }
        }
        return length;
    }

    /**
     * Get value of entity field.
     *
     * @param prefix  prefix of field path
     * @param mapData map field path and data of entity
     * @param field   field of entity
     * @return value of field
     */
    public static Object convertMapEntityPathAndValueToEntity(String prefix, Map<String, Object> mapData, Field field) {
        if (null != mapData && null != field) {
            if (!StringUtils.isEmpty(prefix)) {
                prefix += Constants.DOT;
            }
            Class<?> fieldType = MappingUtils.getFieldType(field);
            if (validate(fieldType)) {
                if (!ObjectUtils.fieldIsCollection(field)) {
                    return convertMapEntityPathAndValueToEntity(prefix + field.getName(), mapData, fieldType);
                } else {
                    int length = countLengthOfArray(prefix + field.getName(), mapData);
                    if (length > 0) {
                        try {
                            @SuppressWarnings("unchecked")
                            Collection<Object> collection = ObjectUtils.newInstanceCollection(field.getType());
                            for (int i = 0; i < length; i++) {
                                Object innerEntity = convertMapEntityPathAndValueToEntity(prefix + field.getName() + "[" + i + "]", mapData, fieldType);
                                if (null != innerEntity) {
                                    collection.add(innerEntity);
                                }
                            }
                            return collection;
                        } catch (NoSuchMethodException e) {
                            throw new ConstructorInvalidException("Cannot new collection instance for field " + field.getName(), e);
                        }
                    }
                }
            } else {
                return mapData.get(prefix + field.getName());
            }
        }
        return null;
    }

    /**
     * Convert map field path and value to entity.
     *
     * @param prefix     prefix of field path
     * @param mapData    map data and value of entity
     * @param entityType entity type
     * @param <T>        generic of entity
     * @return entity
     */
    public static <T> T convertMapEntityPathAndValueToEntity(String prefix, Map<String, Object> mapData, Class<T> entityType) {
        if (null != mapData && null != entityType) {
            validateThrow(entityType, new ConfigurationInvalidException(entityType.getName() + ": Entity configuration is invalid"));

            T entity;
            try {
                entity = ObjectUtils.newInstance(entityType);
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                throw new ConstructorInvalidException("Cannot found default constructor for " + entityType.getSimpleName(), e);
            }

            List<Field> fields = ObjectUtils.getFields(entityType, true);
            for (Field field : fields) {
                try {
                    Object fieldValue = convertMapEntityPathAndValueToEntity(prefix, mapData, field);
                    ObjectUtils.setValueForField(entity, field.getName(), fieldValue);
                } catch (IllegalAccessException e) {
                    throw new FieldInaccessibleException("Cannot set value for field "
                            + entity.getClass().getSimpleName() + "." + field.getName(), e);
                }
            }
            return entity;
        }
        return null;
    }

}
