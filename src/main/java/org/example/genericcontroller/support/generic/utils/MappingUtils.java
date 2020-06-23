package org.example.genericcontroller.support.generic.utils;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.exception.generic.ConfigurationInvalidException;
import org.example.genericcontroller.utils.ObjectUtils;
import org.example.genericcontroller.utils.constant.Constants;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.Tuple;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public static List<String> getEntityMappingFieldPaths(Class<?> dtoType, String[] filter, boolean collection) {
        DataTransferObjectUtils.validateThrow(dtoType, new ConfigurationInvalidException(dtoType.getName() + ": Data Transfer Object configuration is invalid"));
        Class<? extends Audit> entityType = DataTransferObjectUtils.getEntityType(dtoType);
        EntityUtils.validateThrow(entityType, new ConfigurationInvalidException(entityType.getName() + ": Entity configuration is invalid"));
        List<String> entityFieldPaths;

        if (!collection) {
            entityFieldPaths = DataTransferObjectUtils.getEntityMappingFieldPaths(dtoType, true, false);
        } else {
            entityFieldPaths = DataTransferObjectUtils.getEntityMappingFieldPathsCollection(dtoType, true);
        }

        entityFieldPaths = filterFieldPath(
                entityFieldPaths,
                DataTransferObjectUtils.getEntityMappingFieldPathsPrimary(dtoType, false),
                filter);
        return entityFieldPaths;
    }

    /**
     * Filter field path.
     *
     * @param fieldPaths list field path
     * @param filter     list field should be keeping
     * @return list field path after filter
     */
    public static List<String> filterFieldPath(List<String> fieldPaths, List<String> keyFieldPaths, String[] filter) {
        if (!ObjectUtils.isEmpty(filter)) {
            int index = 0;
            while (index < fieldPaths.size()) {
                String entityFieldPath = fieldPaths.get(index);

                if (isKeepField(entityFieldPath, filter)) {
                    index++;
                } else {
                    fieldPaths.remove(index);
                }
            }
        }
        if (!CollectionUtils.isEmpty(keyFieldPaths)) {
            fieldPaths.addAll(keyFieldPaths);
            fieldPaths = fieldPaths.stream().distinct().collect(Collectors.toList());
        }
        return fieldPaths;
    }

    /**
     * Check field is include or exclude.
     *
     * @param fieldPath field path
     * @param filter    filter field
     * @return true if field is matching any value in filter array
     */
    public static boolean isKeepField(String fieldPath, String[] filter) {
        boolean keep = !StringUtils.isEmpty(fieldPath) && null == filter;
        if (!keep && null != filter) {
            for (String keepField : filter) {
                if (fieldPath.equals(keepField) || fieldPath.startsWith(keepField.concat(Constants.DOT))) {
                    keep = true;
                    break;
                }
            }
        }
        return keep;
    }

    /**
     * Convert list tuple to list map record.
     *
     * @param tuples  list tuple
     * @param aliases list alias
     * @return list map record
     */
    public static List<Map<String, Object>> convertTupleToMapRecord(List<Tuple> tuples, List<String> aliases) {
        List<Map<String, Object>> records = new ArrayList<>();
        if (!CollectionUtils.isEmpty(tuples) && !CollectionUtils.isEmpty(aliases)) {
            for (Tuple tuple : tuples) {
                Map<String, Object> record = new HashMap<>();
                for (String alias : aliases) {
                    record.put(alias, tuple.get(alias));
                }
                records.add(record);
            }
        }
        return records;
    }

    /**
     * Convert list map record to list object Data Transfer Object.
     *
     * @param records list map record
     * @param dtoType Data Transfer Object type
     * @return list Data Transfer Object
     */
    public static List<Object> convertToListDataTransferObject(List<Map<String, Object>> records, Class<?> dtoType, String[] filter) {
        Map<String, Object> mapDTO = new LinkedHashMap<>();
        if (!CollectionUtils.isEmpty(records) && null != dtoType) {
            for (Map<String, Object> record : records) {
                String key = DataTransferObjectUtils.getKey(Constants.EMPTY_STRING, dtoType, record);
                Object dto = mapDTO.get(key);
                dto = DataTransferObjectUtils.convertToDataTransferObject(dto, Constants.EMPTY_STRING, record, dtoType, filter);
                mapDTO.put(key, dto);
            }
        }
        return new ArrayList<>(mapDTO.values());
    }

    /**
     * Merge records with collection fields.
     *
     * @param records    the record data
     * @param collection the record data of collection field
     * @param dtoType    Data Transfer Object type
     * @return list record after merged
     */
    public static List<Map<String, Object>> merge(List<Map<String, Object>> records,
                                                  List<Map<String, Object>> collection, Class<?> dtoType) {
        List<Map<String, Object>> merged = new ArrayList<>();
        if (null != dtoType) {
            Map<String, List<Map<String, Object>>> mapCollectionById = new HashMap<>();
            if (!CollectionUtils.isEmpty(collection)) {
                for (Map<String, Object> collect : collection) {
                    String key = DataTransferObjectUtils.getKey(Constants.EMPTY_STRING, dtoType, collect);
                    List<Map<String, Object>> list = mapCollectionById.get(key);
                    if (null == list) {
                        list = new ArrayList<>();
                    }
                    list.add(collect);
                    mapCollectionById.put(key, list);
                }
            }
            if (!CollectionUtils.isEmpty(records)) {
                for (Map<String, Object> record : records) {
                    String key = DataTransferObjectUtils.getKey(Constants.EMPTY_STRING, dtoType, record);
                    List<Map<String, Object>> list = mapCollectionById.get(key);
                    merged.addAll(buildMergeRecord(record, list));
                }
            }
        }
        return merged;
    }

    /**
     * Build merge record.
     *
     * @param record     record data
     * @param collection list collection field of record
     * @return list record merged
     */
    private static List<Map<String, Object>> buildMergeRecord(Map<String, Object> record, List<Map<String, Object>> collection) {
        List<Map<String, Object>> listBuild = new ArrayList<>();
        if (!CollectionUtils.isEmpty(collection)) {
            for (Map<String, Object> collect : collection) {
                collect.putAll(record);
                listBuild.add(collect);
            }
        } else {
            listBuild.add(new HashMap<>(record));
        }
        return listBuild;
    }

    /**
     * Get Field Type.
     *
     * @param field Field
     * @return field type
     */
    public static Class<?> getFieldType(Field field) {
        if (null != field) {
            Class<?> innerClass = field.getType();
            // Override inner class if field is collection
            if (ObjectUtils.fieldIsCollection(field)) {
                innerClass = ObjectUtils.getGenericField(field);
            }
            return innerClass;
        }
        return null;
    }

    /**
     * Convert Data Transfer Object to Entity.
     *
     * @param dto        Data Transfer Object instance
     * @param entityType Entity type
     * @param <T>        generic of entity
     * @return Entity
     */
    public static <T> T convertDataTransferObjectToEntity(Object dto, Class<T> entityType) {
        Class<?> entityTypeDTO = DataTransferObjectUtils.getEntityType(dto.getClass());
        if (!entityType.isAssignableFrom(entityTypeDTO)) {
            throw new ConfigurationInvalidException("Repository of class '" + entityType.getName() + "' cannot process for '" + entityTypeDTO.getName() + "' class");
        }
        return null;
    }
}
