package org.example.genericcontroller.support.generic.mapping;

import org.example.genericcontroller.support.generic.MappingClass;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;

import javax.persistence.Entity;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FieldMapping {

    private GenericField dtoField;

    private Queue<GenericField> entityFieldQueue;

    private String mappingPath;

    private FieldMapping(Field dtoField, Field entityField, String mappingPath) {
        Assert.notNull(dtoField, "Data Transfer Object field must be not null");
        Assert.notNull(entityField, "Entity field must be not null");
        this.mappingPath = mappingPath;
        this.dtoField = GenericField.of(dtoField);
        this.entityFieldQueue = new LinkedList<>();
        this.entityFieldQueue.add(GenericField.of(entityField));
    }

    private FieldMapping(Field dtoField, List<Field> entityFields, String mappingPath) {
        Assert.notNull(dtoField, "Data Transfer Object field must be not null");
        Assert.notEmpty(entityFields, "Entity field must be not empty");
        this.mappingPath = mappingPath;
        this.dtoField = GenericField.of(dtoField);
        this.entityFieldQueue = new LinkedList<>();
        for (Field entityField : entityFields) {
            this.entityFieldQueue.add(GenericField.of(entityField));
        }
    }

    public GenericField getDtoField() {
        return dtoField;
    }

    public Queue<GenericField> getEntityFieldQueue() {
        return new LinkedList<>(entityFieldQueue);
    }

    public String getMappingPath() {
        return mappingPath;
    }

    public static FieldMapping of(String mappingPath, Field dtoField, Field entityField) {
        return new FieldMapping(dtoField, entityField, mappingPath);
    }

    public static FieldMapping of(String mappingPath, Field dtoField, Field... entityField) {
        return new FieldMapping(dtoField, Arrays.asList(entityField), mappingPath);
    }

    @Override
    public String toString() {
        return "FieldMapping{" +
                "mappingPath='" + mappingPath + '\'' +
                '}';
    }
}
