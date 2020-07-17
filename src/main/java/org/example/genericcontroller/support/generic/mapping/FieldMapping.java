package org.example.genericcontroller.support.generic.mapping;

import org.example.genericcontroller.support.generic.MappingClass;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;

import javax.persistence.Entity;
import java.lang.reflect.Field;

public class FieldMapping {

    private GenericField dtoField;

    private GenericField entityField;

    private String mappingPath;

    private FieldMapping(Field dtoField, Field entityField, String mappingPath) {
        Assert.notNull(dtoField, "Data Transfer Object field must be not null");
        Assert.notNull(entityField, "Entity field must be not null");
        this.mappingPath = mappingPath;
        this.dtoField = GenericField.of(dtoField);
        this.entityField = GenericField.of(entityField);
    }

    public GenericField getDtoField() {
        return dtoField;
    }

    public GenericField getEntityField() {
        return entityField;
    }

    public String getMappingPath() {
        return mappingPath;
    }

    public boolean isDTOField() {
        return AnnotatedElementUtils.hasAnnotation(dtoField.getClassDeclaring(), MappingClass.class);
    }

    public boolean isEntityField() {
        return AnnotatedElementUtils.hasAnnotation(entityField.getClassDeclaring(), Entity.class);
    }

    public static FieldMapping of(Field dtoField, Field entityField, String mappingPath) {
        return new FieldMapping(dtoField, entityField, mappingPath);
    }

    @Override
    public String toString() {
        return "FieldMapping{" +
                "mappingPath='" + mappingPath + '\'' +
                '}';
    }
}
