package org.example.genericcontroller.support.generic.utils;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.exception.generic.EntityInvalidException;
import org.example.genericcontroller.support.generic.MappingClass;
import org.example.genericcontroller.utils.ObjectUtils;
import org.example.genericcontroller.utils.constant.Constants;
import org.springframework.util.CollectionUtils;

import javax.persistence.Tuple;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static List<String> getEntityMappingFieldPaths(Class<?> dtoType, String[] filter) {
        List<String> entityFieldPaths = new ArrayList<>();
        if (ObjectUtils.hasAnnotation(dtoType, MappingClass.class)) {
            Class<? extends Audit> entityType = ObjectUtils.getAnnotation(dtoType, MappingClass.class).value();
            EntityUtils.validateThrow(entityType, new EntityInvalidException("Entity configuration is invalid"));

            entityFieldPaths = filterFieldPath(DataTransferObjectUtils.getEntityMappingFieldPaths(dtoType, true), filter);
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
    public static List<String> filterFieldPath(List<String> fieldPaths, String[] filter) {
        if (!ObjectUtils.isEmpty(filter)) {
            int index = 0;
            while (index < fieldPaths.size()) {
                String entityFieldPath = fieldPaths.get(index);

                boolean keep = false;
                for (String keepField : filter) {
                    if (entityFieldPath.equals(keepField) || entityFieldPath.startsWith(keepField.concat(Constants.DOT))) {
                        keep = true;
                        break;
                    }
                }

                if (keep) {
                    index++;
                } else {
                    fieldPaths.remove(index);
                }
            }
        }
        return fieldPaths;
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

//    public static String getKeyOfDataTransferObject(Class<?> dtoType, Map<String, Object> record) {
//        StringBuilder key = new StringBuilder(Constants.EMPTY_STRING);
//        if (null != dtoType) {
//            List<Field> fields = ObjectUtils.getFields(dtoType, true);
//            for (Field field : fields) {
//                Class<?> innerClass = field.getType();
//                // Override inner class if field is collection
//                if (ObjectUtils.fieldIsCollection(field)) {
//                    innerClass = ObjectUtils.getGenericField(field);
//                }
//
//                if (!validate(innerClass)) {
//                    String fieldPath = getEntityMappingFieldPath(field);
//                    Object fieldValue = record.get(fieldPath);
//                    key.append(fieldValue.toString());
//                }
//            }
//        }
//        return key.toString();
//    }

    public static List<Object> convertToListDataTransferObject(List<Map<String, Object>> records, Class<?> dtoType)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        if (!CollectionUtils.isEmpty(records) && null != dtoType) {
            Map<String, Object> mapDTO = new HashMap<>();
            for (Map<String, Object> record : records) {
                DataTransferObjectUtils.convertDataToDataToDataTransferObject(mapDTO, Constants.EMPTY_STRING, record, dtoType);
//                String key = DataTransferObjectUtils.getKeyOfDataTransferObject(dtoType, record);
//                System.out.println(key);
            }
//            Object dto = ObjectUtils.newInstance(dtoType);
//            List<Field> dtoFields = ObjectUtils.getFields(dtoType, true);
        }
        return null;
    }
}
