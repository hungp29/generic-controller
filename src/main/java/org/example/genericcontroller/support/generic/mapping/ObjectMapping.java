package org.example.genericcontroller.support.generic.mapping;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.support.generic.MappingClass;
import org.example.genericcontroller.support.generic.MappingField;
import org.example.genericcontroller.support.generic.ObjectMappingCache;
import org.example.genericcontroller.support.generic.exception.ConfigurationInvalidException;
import org.example.genericcontroller.utils.ObjectUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Object Mapping.
 *
 * @author hungp
 */
public class ObjectMapping {

    private Class<?> dtoType;
    private Class<? extends Audit> entityType;
    private List<FieldMapping> fields;
    private ObjectMappingCache mappingCache;

    private FieldTypeResolved fieldTypeResolved = FieldTypeResolved.getInstance();

    /**
     * New instance of {@link ObjectMapping}.
     *
     * @param dtoType      Class type of DTO
     * @param mappingCache {@link ObjectMappingCache} object mapping cache
     */
    private ObjectMapping(Class<?> dtoType, ObjectMappingCache mappingCache) {
        Assert.notNull(dtoType, "Data Transfer Object class must be not null");
        this.dtoType = dtoType;
        this.mappingCache = mappingCache;
        afterSetProperties();
    }

    /**
     * Process another fields after set properties.
     */
    private void afterSetProperties() {
        // Get entity mapping
        MappingClass mappingClass = AnnotatedElementUtils.findMergedAnnotation(dtoType, MappingClass.class);
        if (null == mappingClass) {
            throw new ConfigurationInvalidException("Cannot found @MappingClass in " + dtoType.getName());
        }
        // Store entity type
        entityType = mappingClass.value();

        // Generate list FieldMapping
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

    /**
     * Static method to new instance {@link ObjectMapping}.
     *
     * @param dtoType      Class type of DTO
     * @param mappingCache {@link ObjectMappingCache} object mapping cache
     * @return new instance of {@link ObjectMapping}
     */
    public static ObjectMapping of(Class<?> dtoType, ObjectMappingCache mappingCache) {
        return new ObjectMapping(dtoType, mappingCache);
    }

    /**
     * Get class name of DTO.
     *
     * @return class name
     */
    public String getDTOClassName() {
        return dtoType.getName();
    }

    /**
     * Get class name of Entity.
     *
     * @return class name
     */
    public String getEntityClassName() {
        return entityType.getName();
    }

    /**
     * Get class type of DTO.
     *
     * @return class type
     */
    public Class<?> getDTOType() {
        return dtoType;
    }

    /**
     * Get class type of entity.
     *
     * @return class type
     */
    public Class<? extends Audit> getEntityType() {
        return entityType;
    }

    /**
     * Get list {@link FieldMapping}.
     *
     * @return list {@link FieldMapping}
     */
    public List<FieldMapping> getFields() {
        return fields;
    }

    public List<String> getListFieldPath(boolean includeCollection) {
        List<String> paths = new LinkedList<>();
        for (FieldMapping fieldMapping : fields) {
            paths.addAll(fieldMapping.getListFieldPath(includeCollection));
        }
        return paths.stream().distinct().collect(Collectors.toList());
    }

    public List<String> getListCollectionFieldPath() {
        List<String> paths = new LinkedList<>();
        for (FieldMapping fieldMapping : fields) {
            paths.addAll(fieldMapping.getListCollectionFieldPath());
        }
        return paths.stream().distinct().collect(Collectors.toList());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectMapping that = (ObjectMapping) o;
        return Objects.equals(dtoType.getName(), that.dtoType.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(dtoType, entityType, fields, fieldTypeResolved);
    }

    @Override
    public String toString() {
        return "ObjectMapping{" +
                "dtoType=" + dtoType.getName() +
                ", entityType=" + entityType.getName() +
                '}';
    }
}
