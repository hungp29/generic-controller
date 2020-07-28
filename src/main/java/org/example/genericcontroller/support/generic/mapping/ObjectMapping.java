package org.example.genericcontroller.support.generic.mapping;

import org.example.genericcontroller.entity.Audit;
import org.example.genericcontroller.support.generic.FilterData;
import org.example.genericcontroller.support.generic.MappingClass;
import org.example.genericcontroller.support.generic.MappingField;
import org.example.genericcontroller.support.generic.ObjectMappingCache;
import org.example.genericcontroller.support.generic.exception.ConfigurationInvalidException;
import org.example.genericcontroller.utils.ObjectUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Selection;
import java.lang.reflect.Field;
import java.util.ArrayList;
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

    /**
     * Get list field path.
     *
     * @param includeCollection flag to detect get collection fields
     * @return list field path
     */
    public List<String> getListFieldPath(boolean includeCollection) {
        List<String> paths = new LinkedList<>();
        for (FieldMapping fieldMapping : fields) {
            paths.addAll(fieldMapping.getListFieldPath(includeCollection));
        }
        return paths.stream().distinct().collect(Collectors.toList());
    }

    /**
     * Get only path of collection fields.
     *
     * @return list field path of collection fields
     */
    public List<String> getListCollectionFieldPath() {
        List<String> paths = new LinkedList<>();
        for (FieldMapping fieldMapping : fields) {
            if (fieldMapping.isCollection()) {
                paths.addAll(fieldMapping.getListFieldPath(true));
            }
        }
        return paths.stream().distinct().collect(Collectors.toList());
    }

    /**
     * Get selection method.
     *
     * @param from          {@link From} instance
     * @param selectionType {@link SelectionType} selection type
     * @param prefixAlias   prefix of alias
     * @return list {@link Selection} by selection type
     */
    List<Selection<?>> getSelections(From<?, ?> from, SelectionType selectionType, FilterData filterData, String prefixAlias) {
        Assert.notNull(prefixAlias, "Prefix alias cannot be null!");
        List<Selection<?>> selections = new ArrayList<>();
        for (FieldMapping fieldMapping : fields) {
            // SelectionType describe:
            // ALL_FIELD: Get all field
            // COLLECTION_FIELD: Get id, sub id and collection field
            // NONE_COLLECTION_FIELD: Get id and field which is not collection
            selections.addAll(fieldMapping.getSelections(from, selectionType, filterData, prefixAlias));
        }
        return selections.stream().distinct().collect(Collectors.toList());
    }

    /**
     * Get selection of fields which are not collection fields.
     *
     * @param from {@link From} instance
     * @return list {@link Selection}
     */
    public List<Selection<?>> getNoneCollectionSelections(From<?, ?> from, FilterData filterData) {
        return getSelections(from, SelectionType.NONE_COLLECTION_FIELD, filterData, "");
    }

    /**
     * Get selection of fields which are collection fields.
     *
     * @param from {@link From} instance
     * @return list {@link Selection}
     */
    public List<Selection<?>> getCollectionSelections(From<?, ?> from, FilterData filterData) {
        return getSelections(from, SelectionType.COLLECTION_FIELD, filterData, "");
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

    /**
     * Selection Type.
     */
    public enum SelectionType {
        ALL_FIELD,              // Get all field
        ID_FIELD,               // Get only id field
        COLLECTION_FIELD,       // Get id and collection field
        NONE_COLLECTION_FIELD   // Get id and normal field (ignore collection)
    }
}
