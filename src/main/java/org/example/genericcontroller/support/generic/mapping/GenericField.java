package org.example.genericcontroller.support.generic.mapping;

import org.springframework.util.Assert;

import java.lang.reflect.Field;

public class GenericField {

    private Field field;

    private GenericField(Field field) {
        Assert.notNull(field, "Generic field must be not null");
        this.field = field;
    }

    public Field getField() {
        return field;
    }

    public String getClassName() {
        return field.getType().getName();
    }

    public Class<?> getClassDeclaring() {
        return field.getType();
    }

    public static GenericField of(Field field) {
        return new GenericField(field);
    }
}
