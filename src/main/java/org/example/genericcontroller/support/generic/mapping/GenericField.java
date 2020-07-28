package org.example.genericcontroller.support.generic.mapping;

import org.example.genericcontroller.support.generic.MappingClass;
import org.example.genericcontroller.support.generic.ObjectMappingCache;
import org.example.genericcontroller.utils.ObjectUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;

import javax.persistence.Entity;
import java.lang.reflect.Field;
import java.util.Collection;

/**
 * Generic Field.
 *
 * @author hungp
 */
public class GenericField {

    private GenericField parentField;
    private ObjectMappingCache mappingCache;
    private Field field;
    private Class<?> fieldType;
    private boolean isCollection;
    @SuppressWarnings("rawtypes")
    private Class<? extends Collection> collectionType;

    /**
     * New instance of {@link GenericField}.
     *
     * @param field        {@link Field} instance
     * @param parentField  {@link GenericField} parent field
     * @param mappingCache {@link ObjectMappingCache} cache of {@link ObjectMapping}
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private GenericField(Field field, GenericField parentField, ObjectMappingCache mappingCache) {
        Assert.notNull(field, "Generic field must be not null");
        this.field = field;
        this.parentField = parentField;
        this.mappingCache = mappingCache;
        if (Collection.class.isAssignableFrom(field.getType())) {
            isCollection = true;
            collectionType = (Class<? extends Collection>) field.getType();
            fieldType = ObjectUtils.getGenericField(field);
        } else {
            fieldType = field.getType();
        }
    }

    /**
     * Get {@link Field} instance.
     *
     * @return {@link Field}
     */
    public Field getField() {
        return field;
    }

    /**
     * Get Field name.
     *
     * @return field name
     */
    public String getFieldName() {
        return field.getName();
    }

    /**
     * Get class name of field.
     *
     * @return class name of field
     */
    public String getFieldClassName() {
        return fieldType.getName();
    }

    /**
     * Get class of field.
     *
     * @return the class of field
     */
    public Class<?> getFieldClass() {
        return fieldType;
    }

    /**
     * Get declare class contain field.
     *
     * @return declare class
     */
    public String getDeclareClassName() {
        return field.getDeclaringClass().getName();
    }

    /**
     * Checking field is collection or not.
     *
     * @return true if field is collection
     */
    public boolean isCollection() {
        return isCollection;
    }

    /**
     * Checking field is inner object (DTO, Entity).
     *
     * @return true if field is inner object
     */
    public boolean isInnerObject() {
        return isInnerDTO() || isInnerEntity();
    }

    /**
     * Checking field is inner DTO (only DTO and having annotation {@link MappingClass}.
     *
     * @return true if field is inner DTO
     */
    public boolean isInnerDTO() {
        return AnnotatedElementUtils.hasAnnotation(fieldType, MappingClass.class);
    }

    /**
     * Checking field is inner entity (only Entity and having annotation {@link Entity}.
     *
     * @return true if field is inner Entity
     */
    public boolean isInnerEntity() {
        return AnnotatedElementUtils.hasAnnotation(fieldType, Entity.class);
    }

    /**
     * Static method to new {@link GenericField}.
     *
     * @param field        {@link Field} instance
     * @param parentField  {@link GenericField} parent field
     * @param mappingCache {@link ObjectMappingCache} cache of {@link ObjectMapping}
     * @return new instance of {@link GenericField}
     */
    public static GenericField of(Field field, GenericField parentField, ObjectMappingCache mappingCache) {
        return new GenericField(field, parentField, mappingCache);
    }

    @Override
    public String toString() {
        return "GenericField{" +
                "field=" + field.getName() +
                '}';
    }
}
