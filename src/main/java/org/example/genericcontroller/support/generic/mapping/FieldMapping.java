package org.example.genericcontroller.support.generic.mapping;

import org.example.genericcontroller.support.generic.DTOMappingCache;
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

    private DTOMappingCache mappingCache;

    private FieldMapping(Field dtoField, List<Field> entityFields, String mappingPath, DTOMappingCache mappingCache) {
        Assert.notNull(dtoField, "Data Transfer Object field must be not null");
        Assert.notEmpty(entityFields, "Entity field must be not empty");
        this.mappingPath = mappingPath;
        this.mappingCache = mappingCache;
        this.dtoField = GenericField.of(dtoField, mappingCache);
        this.entityFieldQueue = new LinkedList<>();
        for (Field entityField : entityFields) {
            this.entityFieldQueue.add(GenericField.of(entityField, mappingCache));
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

    public static FieldMapping of(String mappingPath, DTOMappingCache mappingCache, Field dtoField, Field... entityField) {
        return new FieldMapping(dtoField, Arrays.asList(entityField), mappingPath, mappingCache);
    }

    @Override
    public String toString() {
        return "FieldMapping{" +
                "mappingPath='" + mappingPath + '\'' +
                '}';
    }
}
