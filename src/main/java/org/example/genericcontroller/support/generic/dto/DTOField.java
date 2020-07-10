package org.example.genericcontroller.support.generic.dto;

import org.example.genericcontroller.support.generic.MappingField;
import org.example.genericcontroller.utils.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;

/**
 * DTO Field.
 *
 * @author hungp
 */
public class DTOField extends GenericField {

    /**
     * Prevent new instance.
     */
    private DTOField(Field field) {
        this.field = field;
    }

    /**
     * Get mapping Entity field.
     *
     * @return entity field
     */
    public String getMappingEntityField() {
        String fieldPath = field.getName();
        MappingField mappingField = ObjectUtils.getAnnotation(field, MappingField.class);
        if (null != mappingField && !StringUtils.isEmpty(mappingField.entityField())) {
            fieldPath = mappingField.entityField();
        }
        return fieldPath;
    }

    /**
     * Create new instance of {@link DTOField} from {@link Field}.
     *
     * @param field {@link Field} instance
     * @return {@link DTOField} instance
     */
    public static DTOField of(Field field) {
        return new DTOField(field);
    }
}
