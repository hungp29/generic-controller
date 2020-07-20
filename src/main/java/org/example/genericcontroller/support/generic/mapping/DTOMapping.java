package org.example.genericcontroller.support.generic.mapping;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.support.generic.DTOMappingCache;
import org.example.genericcontroller.support.generic.MappingClass;
import org.example.genericcontroller.support.generic.MappingField;
import org.example.genericcontroller.support.generic.exception.ConfigurationInvalidException;
import org.example.genericcontroller.utils.ObjectUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class DTOMapping {

    private Class<?> dtoType;

    private Class<? extends Audit> entityType;

    private List<FieldMapping> fields;

    private DTOMappingCache mappingCache;

    private FieldTypeResolved fieldTypeResolved = FieldTypeResolved.getInstance();

    private DTOMapping(Class<?> dtoType, DTOMappingCache mappingCache) {
        Assert.notNull(dtoType, "Data Transfer Object class must be not null");
        this.dtoType = dtoType;
        this.mappingCache = mappingCache;
        afterSetProperties();
    }

    public String getDTOClassName() {
        return dtoType.getName();
    }

    public String getEntityClassName() {
        return entityType.getName();
    }

    public Class<?> getDtoType() {
        return dtoType;
    }

    public Class<? extends Audit> getEntityType() {
        return entityType;
    }

    public List<FieldMapping> getFields() {
        return fields;
    }

    public static DTOMapping of(Class<?> dtoType, DTOMappingCache mappingCache) {
        return new DTOMapping(dtoType, mappingCache);
    }

    private void afterSetProperties() {
        // Get entity mapping
        MappingClass mappingClass = AnnotatedElementUtils.findMergedAnnotation(dtoType, MappingClass.class);
        if (null == mappingClass) {
            throw new ConfigurationInvalidException("Cannot found @MappingClass in " + dtoType.getName());
        }
        // Store entity type
        entityType = mappingClass.value();

        fields = new LinkedList<>();
        for (Field field : ObjectUtils.getFields(dtoType, true)) {
            String path = field.getName();
            MappingField mappingField = AnnotatedElementUtils.findMergedAnnotation(field, MappingField.class);
            if (null != mappingField && !StringUtils.isEmpty(mappingField.entityField())) {
                path = mappingField.entityField();
            }
            Field[] entityFieldPath = fieldTypeResolved.parseFieldByPath(entityType, path);
            fields.add(FieldMapping.of(path, mappingCache, field, entityFieldPath));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DTOMapping that = (DTOMapping) o;
        return Objects.equals(dtoType.getName(), that.dtoType.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(dtoType, entityType, fields, fieldTypeResolved);
    }

    @Override
    public String toString() {
        return "DTOMapping{" +
                "dtoType=" + dtoType.getName() +
                '}';
    }
}
