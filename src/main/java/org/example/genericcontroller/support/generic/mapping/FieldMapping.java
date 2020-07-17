package org.example.genericcontroller.support.generic.mapping;

import org.springframework.util.Assert;

import java.lang.reflect.Field;

public class FieldMapping {

    private GenericField dtoField;

    private GenericField entityField;

    private FieldMapping(Field dtoField, Field entityField) {
        Assert.notNull(dtoField, "Data Transfer Object field must be not null");
        Assert.notNull(entityField, "Entity field must be not null");
        this.dtoField = GenericField.of(dtoField);
        this.entityField = GenericField.of(entityField);
    }

    public static FieldMapping of(Field dtoField, Field entityField) {
        return new FieldMapping(dtoField, entityField);
    }
}
