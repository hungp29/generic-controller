package org.example.genericcontroller.support.defaulthttp;

import org.example.genericcontroller.utils.ObjectUtils;
import org.example.genericcontroller.utils.constant.Constants;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class Converter {

    private Converter() {
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

    public static String getEntityFieldNameByDTOField(Field dtoField) {
        String entityFieldName = Constants.EMPTY_STRING;
        if (null != dtoField) {
            MapField mapField = ObjectUtils.getAnnotation(dtoField, MapField.class);
            if (null != mapField && !StringUtils.isEmpty(mapField.entityField())) {
                entityFieldName = mapField.entityField();
            } else {
                entityFieldName = dtoField.getName();
            }
        }
        return entityFieldName;
    }
}
