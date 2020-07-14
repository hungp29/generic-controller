package org.example.genericcontroller.support.generic.obj;

import org.example.genericcontroller.support.generic.MappingField;
import org.example.genericcontroller.utils.ObjectUtils;
import org.example.genericcontroller.utils.constant.Constants;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Generic Field.
 *
 * @author hungp
 */
public abstract class GenericField {

    protected Field field;

    protected boolean isCollection;

    protected boolean isInnerDTO;


    /**
     * Get Mapping field name.
     *
     * @return mappinf field name
     */
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

    /**
     * Get list mapping field.
     *
     * @param lookingInner      looking inner DTO
     * @param includeCollection include field of collection field
     * @return list mapping field
     */
    public abstract List<String> getMappingFieldPath(boolean lookingInner, boolean includeCollection);

    public boolean isCollection() {
        return isCollection;
    }

    public boolean isInnerDTO() {
        return isInnerDTO;
    }
}
