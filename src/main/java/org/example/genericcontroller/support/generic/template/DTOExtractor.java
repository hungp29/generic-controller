package org.example.genericcontroller.support.generic.template;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.support.generic.MappingClass;
import org.example.genericcontroller.support.generic.MappingField;
import org.example.genericcontroller.support.generic.exception.ConfigurationInvalidException;
import org.example.genericcontroller.utils.ObjectUtils;
import org.example.genericcontroller.utils.constant.Constants;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * DTO Extractor.
 *
 * @author hungp
 */
public class DTOExtractor {

    private Class<? extends DTOTemplate> dtoType;
    private Class<? extends Audit> entityType;
    private List<Field> fields;

    /**
     * Prevent new instance.
     */
    private DTOExtractor() {
    }

    public Class<?> getDtoType() {
        return dtoType;
    }

    @SuppressWarnings("unchecked")
    public void setDtoType(Class<?> dtoType) {
        if (!DTOTemplate.class.isAssignableFrom(dtoType)) {
            throw new ConfigurationInvalidException(dtoType.getName() + " don't extended " + DTOTemplate.class.getName());
        }
        this.dtoType = (Class<? extends DTOTemplate>) dtoType;
        this.entityType = getEntityMapping();
        this.fields = ObjectUtils.getFields(dtoType, true);
    }

    public List<Field> getFields() {
        return fields;
    }

    /**
     * Get mapping entity field path.
     *
     * @return list field path of entity mapped
     */
    public List<String> getMappingFieldPath(boolean lookingInner, boolean includeCollection) {
        List<String> paths = new ArrayList<>();
        for (Field field : fields) {
            Class<?> fieldType = getFieldType(field);
            String fieldName = getMappingFieldName(field);
            if (!lookingInner(field, lookingInner, includeCollection)) {
                paths.add(fieldName);
            } else {
                DTOExtractor.of(fieldType).getMappingFieldPath(true, includeCollection).forEach(innerFieldPath -> {
                    String fieldPath = fieldName + Constants.DOT + innerFieldPath;
                    if (!paths.contains(fieldPath)) {
                        paths.add(fieldPath);
                    }
                });
            }
        }
        return paths;
    }

    public List<String> getMappingPrimaryFieldPath(boolean includeCollection) {
        List<String> paths = new ArrayList<>();
        for (Field field : fields) {

        }
        return paths;
    }

    /**
     * Check field need to looking inner and get inner fields.
     *
     * @param field DTO field
     * @return true if field type is extend from {@link DTOTemplate}
     */
    private boolean lookingInner(Field field, boolean lookingInner, boolean includeCollection) {
        Class<?> fieldType = getFieldType(field);
        return lookingInner
                && DTOTemplate.class.isAssignableFrom(fieldType)
                && (includeCollection || !Collection.class.isAssignableFrom(field.getType()));
    }

    /**
     * Get entity mapping field.
     *
     * @param field DTO field
     * @return entity mapping field
     */
    private String getMappingFieldName(Field field) {
        String fieldPath = field.getName();
        MappingField mappingField = ObjectUtils.getAnnotation(field, MappingField.class);
        if (null != mappingField && !StringUtils.isEmpty(mappingField.entityField())) {
            fieldPath = mappingField.entityField();
        }
        return fieldPath;
    }

    /**
     * Get field type.
     *
     * @param field DTO field
     * @return field type
     */
    private Class<?> getFieldType(Field field) {
        Class<?> innerClass = field.getType();
        // Override inner class if field is collection
        if (ObjectUtils.fieldIsCollection(field)) {
            innerClass = ObjectUtils.getGenericField(field);
        }
        return innerClass;
    }

    /**
     * Get entity mapping.
     *
     * @return entity type
     */
    public Class<? extends Audit> getEntityMapping() {
        MappingClass mappingClass = ObjectUtils.getAnnotation(dtoType, MappingClass.class);
        if (null != mappingClass) {
            return mappingClass.value();
        }
        throw new ConfigurationInvalidException(this.getClass().getName() + " isn't config MappingClass");
    }

    /**
     * Create new instance {@link DTOExtractor} instance from DTO type.
     *
     * @param dtoType DTO Type
     * @return {@link DTOExtractor} instance
     */
    public static DTOExtractor of(Class<?> dtoType) {
        DTOExtractor extractor = new DTOExtractor();
        extractor.setDtoType(dtoType);
        return extractor;
    }
}
