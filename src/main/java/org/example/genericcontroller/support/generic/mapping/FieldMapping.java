package org.example.genericcontroller.support.generic.mapping;

import org.springframework.util.Assert;

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

    public static FieldMapping of(Field dtoField, Field entityField, String mappingPath) {
        return new FieldMapping(dtoField, entityField, mappingPath);
    }
}
