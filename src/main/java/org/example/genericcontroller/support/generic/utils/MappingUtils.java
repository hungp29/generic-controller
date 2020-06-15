package org.example.genericcontroller.support.generic.utils;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.exception.generic.EntityInvalidException;
import org.example.genericcontroller.support.generic.MappingClass;
import org.example.genericcontroller.utils.ObjectUtils;
import org.example.genericcontroller.utils.constant.Constants;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.HashMap;
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
    public static List<String> getEntityMappingFieldPaths(Class<?> dtoType, String[] filter, boolean count) {
        List<String> entityFieldPaths = new ArrayList<>();
        if (ObjectUtils.hasAnnotation(dtoType, MappingClass.class)) {
            Class<? extends Audit> entityType = ObjectUtils.getAnnotation(dtoType, MappingClass.class).value();
            EntityUtils.validateThrow(entityType, new EntityInvalidException("Entity configuration is invalid"));

            if (!count) {
                entityFieldPaths = filterFieldPath(
                        DataTransferObjectUtils.getEntityMappingFieldPaths(dtoType, true, false),
                        DataTransferObjectUtils.getEntityMappingFieldPathsPrimary(dtoType, false),
                        filter);
            } else {
                entityFieldPaths = DataTransferObjectUtils.getEntityMappingFieldPathsForCount(dtoType);
            }
        }
        return entityFieldPaths;
    }

    public static List<String> getEntityMappingFieldPathsCollection(Class<?> dtoType, String[] filter) {
        List<String> entityFieldPaths = new ArrayList<>();
        if (ObjectUtils.hasAnnotation(dtoType, MappingClass.class)) {
            Class<? extends Audit> entityType = ObjectUtils.getAnnotation(dtoType, MappingClass.class).value();
            EntityUtils.validateThrow(entityType, new EntityInvalidException("Entity configuration is invalid"));
            entityFieldPaths = filterFieldPath(
                    DataTransferObjectUtils.getEntityMappingFieldPathsCollection(dtoType),
                    DataTransferObjectUtils.getEntityMappingFieldPathsPrimary(dtoType, false),
                    filter);
        }
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

//                boolean keep = false;
//                for (String keepField : filter) {
//                    if (entityFieldPath.equals(keepField) || entityFieldPath.startsWith(keepField.concat(Constants.DOT))) {
//                        keep = true;
//                        break;
//                    }
//                }

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

    public static boolean isKeepField(String fieldPath, String[] filter) {
        if (!StringUtils.isEmpty(fieldPath) && null != filter) {
            for (String keepField : filter) {
                if (fieldPath.equals(keepField) || fieldPath.startsWith(keepField.concat(Constants.DOT))) {
                    return true;
                }
            }
        }
        return false;
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

    public static List<Map<String, Object>> merge(List<Map<String, Object>> records, List<Map<String, Object>> collection, Class<?> dtoType) {
        List<Map<String, Object>> merged = new ArrayList<>();
        if (null != dtoType) {
            Map<String, List<Map<String, Object>>> mapCollectionById = new HashMap<>();
            if (!CollectionUtils.isEmpty(collection)) {
                for (Map<String, Object> collect : collection) {
                    String key = DataTransferObjectUtils.getKey(dtoType, collect);
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
                    String key = DataTransferObjectUtils.getKey(dtoType, record);
                    List<Map<String, Object>> list = mapCollectionById.get(key);
                    if (!CollectionUtils.isEmpty(list)) {
                        merged.addAll(buildMerge(record, list));
                    }
                }
            }
        }
        return merged;
    }

    private static List<Map<String, Object>> buildMerge(Map<String, Object> record, List<Map<String, Object>> collection) {
        List<Map<String, Object>> listBuild = new ArrayList<>();
        for (Map<String, Object> collect : collection) {
            Map<String, Object> newMap = new HashMap<>(collect);
            newMap.putAll(record);
            listBuild.add(newMap);
        }
        return listBuild;
    }

    /**
     * Convert list map record to list object Data Transfer Object.
     *
     * @param records list map record
     * @param dtoType Data Transfer Object type
     * @return list Data Transfer Object
     */
    public static List<Object> convertToListDataTransferObject(List<Map<String, Object>> records, Class<?> dtoType, String[] filter) {
        List<Object> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(records) && null != dtoType) {
            list.addAll(DataTransferObjectUtils.convertToListDataTransferObject(records, dtoType, filter));
        }
        return list;
    }
}
