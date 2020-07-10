package org.example.genericcontroller.support.generic.obj;

import org.example.genericcontroller.support.generic.exception.ConfigurationInvalidException;
import org.example.genericcontroller.utils.ObjectUtils;
import org.example.genericcontroller.utils.constant.Constants;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO Object.
 *
 * @author hungp
 */
public class DTOObject extends GenericField {

    private static final String DTO_TYPE_MUST_BE_NOT_NOT = "DTO Type must be not null";
    private static final String FIELD_MUST_BE_NOT_NOT = "DTO Field must be not null";

    private Class<? extends DTOTemplate> type;
    private List<GenericField> dtoFields;

    /**
     * New instance {@link DTOObject} from Class.
     *
     * @param type DTO type
     */
    @SuppressWarnings("unchecked")
    private DTOObject(Class<?> type) {
        if (!validate(type)) {
            throw new ConfigurationInvalidException(type.getName() + " don't extended " + DTOTemplate.class.getName());
        }
        this.type = (Class<? extends DTOTemplate>) type;
        this.dtoFields = buildDTOField();
    }

    /**
     * New instance {@link DTOObject} from {@link Field}.
     *
     * @param field        {@link Field}
     * @param isCollection is collection field
     */
    @SuppressWarnings("unchecked")
    private DTOObject(Field field, boolean isCollection) {
        Class<?> type = getFieldType(field);
        if (!validate(type)) {
            throw new ConfigurationInvalidException(type.getName() + " don't extended " + DTOTemplate.class.getName());
        }
        this.type = (Class<? extends DTOTemplate>) type;
        this.dtoFields = buildDTOField();
        this.field = field;
        this.isCollection = isCollection;
        this.isInnerDTO = true;
    }

    /**
     * Build {@link DTOField}.
     *
     * @return list {@link DTOField}
     */
    private List<GenericField> buildDTOField() {
        List<GenericField> dtoFields = new ArrayList<>();
        for (Field field : ObjectUtils.getFields(type, true)) {
            boolean isCollection = ObjectUtils.fieldIsCollection(field);
            Class<?> fieldType = getFieldType(field);

            if (validate(fieldType)) {
                dtoFields.add(DTOObject.of(field, isCollection));
            } else {
                dtoFields.add(DTOField.of(field));
            }
        }
        return dtoFields;
    }

    /**
     * Validate DTO Type.
     *
     * @param dtoType DTO Type
     * @return true if DTO Type was extended {@link DTOTemplate}
     */
    private boolean validate(Class<?> dtoType) {
        return null != dtoType && DTOTemplate.class.isAssignableFrom(dtoType);
    }

    /**
     * Get field type.
     *
     * @param field {@link Field} instance
     * @return Field type
     */
    private Class<?> getFieldType(Field field) {
        Class<?> innerClass = field.getType();
        // Override inner class if field is collection
        if (ObjectUtils.fieldIsCollection(field)) {
            innerClass = ObjectUtils.getGenericField(field);
        }
        return innerClass;
    }

    @Override
    public List<String> getMappingFieldPath() {
        List<String> paths = new ArrayList<>();
        String mappingFieldName = getMappingFieldName();
        if (!StringUtils.isEmpty(mappingFieldName)) {
            mappingFieldName = mappingFieldName + Constants.DOT;
        }

        final String finalMappingFieldName = mappingFieldName;
        for (GenericField dtoField : dtoFields) {
            dtoField.getMappingFieldPath().forEach(innerPath -> {
                String path = finalMappingFieldName + innerPath;
                if (!paths.contains(path)) {
                    paths.add(path);
                }
            });
        }
        return paths;
    }

    /**
     * Create new instance {@link DTOObject} instance from DTO type.
     *
     * @param dtoType DTO Type
     * @return {@link DTOObject} instance
     */
    public static DTOObject of(Class<?> dtoType) {
        Assert.notNull(dtoType, DTO_TYPE_MUST_BE_NOT_NOT);
        return new DTOObject(dtoType);
    }

    /**
     * Create new instance {@link DTOObject} instance from DTO type.
     *
     * @param field        {@link Field} instance
     * @param isCollection field is collection
     * @return {@link DTOObject} instance
     */
    public static DTOObject of(Field field, boolean isCollection) {
        Assert.notNull(field, FIELD_MUST_BE_NOT_NOT);
        return new DTOObject(field, isCollection);
    }
}
