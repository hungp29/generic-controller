package org.example.genericcontroller.support.generic.obj;

import org.example.genericcontroller.support.generic.MappingField;
import org.example.genericcontroller.utils.ObjectUtils;
import org.example.genericcontroller.utils.constant.Constants;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.List;

public abstract class GenericField {

    protected Field field;

    public String getMappingFieldName() {
        String fieldPath = Constants.EMPTY_STRING;
        if (null != field) {
            fieldPath = field.getName();
            MappingField mappingField = ObjectUtils.getAnnotation(field, MappingField.class);
            if (null != mappingField && !StringUtils.isEmpty(mappingField.entityField())) {
                fieldPath = mappingField.entityField();
            }
        }
        return fieldPath;
    }

    public abstract List<String> getMappingFieldPath();

}
