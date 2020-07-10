package org.example.genericcontroller.support.generic.dto;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.support.generic.MappingClass;
import org.example.genericcontroller.support.generic.exception.ConfigurationInvalidException;
import org.example.genericcontroller.utils.ObjectUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO Extractor.
 *
 * @author hungp
 */
public class DTOObject {

    private Class<? extends DTOTemplate> dtoType;
    private List<DTOField> dtoFields;

    /**
     * Prevent new instance.
     */
    @SuppressWarnings("unchecked")
    private DTOObject(Class<?> dtoType) {
        if (!DTOTemplate.class.isAssignableFrom(dtoType)) {
            throw new ConfigurationInvalidException(dtoType.getName() + " don't extended " + DTOTemplate.class.getName());
        }
        this.dtoType = (Class<? extends DTOTemplate>) dtoType;
        // Build list DTOField object
        this.dtoFields = buildDTOField();
    }

    /**
     * Build {@link DTOField}.
     *
     * @return list {@link DTOField}
     */
    private List<DTOField> buildDTOField() {
        List<DTOField> dtoFields = new ArrayList<>();
        for (Field field : ObjectUtils.getFields(dtoType, true)) {
            dtoFields.add(DTOField.of(field));
        }
        return dtoFields;
    }

    /**
     * Get DTO Type.
     *
     * @return DTO Type
     */
    public Class<?> getDtoType() {
        return dtoType;
    }

    /**
     * Get list {@link DTOField}.
     *
     * @return list {@link DTOField}
     */
    public List<DTOField> getDtoFields() {
        return dtoFields;
    }

//    /**
//     * Get mapping entity field path.
//     *
//     * @return list field path of entity mapped
//     */
//    public List<String> getMappingEntityFieldPath(boolean lookingInner, boolean includeCollection) {
//        List<String> paths = new ArrayList<>();
//        for (Field field : dtoFields) {
//            Class<?> fieldType = getFieldType(field);
//            String fieldName = getMappingFieldName(field);
//            if (!lookingInner(field, lookingInner, includeCollection)) {
//                paths.add(fieldName);
//            } else {
//                DTOExtractor.of(fieldType).getMappingEntityFieldPath(true, includeCollection)
//                        .forEach(innerFieldPath -> {
//                            String fieldPath = fieldName + Constants.DOT + innerFieldPath;
//                            if (!paths.contains(fieldPath)) {
//                                paths.add(fieldPath);
//                            }
//                        });
//            }
//        }
//        return paths;
//    }

//    /**
//     * Get mapping entity primary field path.
//     *
//     * @return list primary field path
//     */
//    public List<String> getMappingEntityPrimaryFieldPath(boolean includeCollection) {
//        List<String> paths = new ArrayList<>(getEntityPrimaryKey());
//        for (Field field : dtoFields) {
//            Class<?> fieldType = getFieldType(field);
//            if (isDTOTemplate(fieldType) && (includeCollection || !ObjectUtils.fieldIsCollection(field))) {
//                String fieldName = getMappingFieldName(field);
//                DTOExtractor.of(fieldType).getEntityPrimaryKey().forEach(innerPrimaryKey -> {
//                    paths.add(fieldName + Constants.DOT + innerPrimaryKey);
//                });
//            }
//        }
//        return paths;
//    }

//    /**
//     * Get Entity primary keys.
//     *
//     * @return list primary keys
//     */
//    private List<String> getEntityPrimaryKey() {
//        List<String> keys = new ArrayList<>();
//        for (Field field : entityFields) {
//            if (ObjectUtils.hasAnnotation(field, Id.class)) {
//                keys.add(field.getName());
//            }
//        }
//        return keys;
//    }

//    /**
//     * Check field need to looking inner and get inner fields.
//     *
//     * @param field DTO field
//     * @return true if field type is extend from {@link DTOTemplate}
//     */
//    private boolean lookingInner(Field field, boolean lookingInner, boolean includeCollection) {
//        Class<?> fieldType = getFieldType(field);
//        return lookingInner
//                && DTOTemplate.class.isAssignableFrom(fieldType)
//                && (includeCollection || !Collection.class.isAssignableFrom(field.getType()));
//    }

//    /**
//     * Get entity mapping field.
//     *
//     * @param field DTO field
//     * @return entity mapping field
//     */
//    private String getMappingFieldName(Field field) {
//        String fieldPath = field.getName();
//        MappingField mappingField = ObjectUtils.getAnnotation(field, MappingField.class);
//        if (null != mappingField && !StringUtils.isEmpty(mappingField.entityField())) {
//            fieldPath = mappingField.entityField();
//        }
//        return fieldPath;
//    }

//    /**
//     * Get field type.
//     *
//     * @param field DTO field
//     * @return field type
//     */
//    private Class<?> getFieldType(Field field) {
//        Class<?> innerClass = field.getType();
//        // Override inner class if field is collection
//        if (ObjectUtils.fieldIsCollection(field)) {
//            innerClass = ObjectUtils.getGenericField(field);
//        }
//        return innerClass;
//    }

//    /**
//     * Check DTO type is extended from {@link DTOTemplate}.
//     *
//     * @param dtoType DTO Type
//     * @return true if DTO type is extended {@link DTOTemplate}
//     */
//    public boolean isDTOTemplate(Class<?> dtoType) {
//        return null != dtoType && DTOTemplate.class.isAssignableFrom(dtoType);
//    }

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
     * Create new instance {@link DTOObject} instance from DTO type.
     *
     * @param dtoType DTO Type
     * @return {@link DTOObject} instance
     */
    public static DTOObject of(Class<?> dtoType) {
        return new DTOObject(dtoType);
    }
}
