package org.example.genericcontroller.support.generic.mapping;

import org.example.genericcontroller.support.generic.ObjectMappingCache;
import org.example.genericcontroller.utils.constant.Constants;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * Field Mapping.
 *
 * @author hungp
 */
public class FieldMapping {

    private GenericField dtoField;
    private LinkedList<GenericField> entityFieldQueue;
    private String mappingPath;
    private ObjectMappingCache mappingCache;

    /**
     * New instance of {@link FieldMapping}.
     *
     * @param dtoField     {@link Field} of DTO
     * @param entityFields {@link Field} of entity
     * @param mappingPath  mapping class
     * @param mappingCache {@link ObjectMappingCache} object mapping cache
     */
    private FieldMapping(Field dtoField, List<Field> entityFields, String mappingPath, ObjectMappingCache mappingCache) {
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

    /**
     * Static method to new instance {@link FieldMapping}.
     *
     * @param mappingPath  mapping path
     * @param mappingCache {@link ObjectMappingCache} object mapping cache
     * @param dtoField     {@link Field} DTO Field
     * @param entityField  {@link Field} Entity Field
     * @return {@link FieldMapping} instance
     */
    public static FieldMapping of(String mappingPath, ObjectMappingCache mappingCache, Field dtoField, Field... entityField) {
        return new FieldMapping(dtoField, Arrays.asList(entityField), mappingPath, mappingCache);
    }

    /**
     * Get DTO Field.
     *
     * @return {@link GenericField} instance
     */
    public GenericField getDTOField() {
        return dtoField;
    }

    public GenericField getLastEntityField() {
        return entityFieldQueue.getLast();
    }

    public Queue<GenericField> getEntityFieldAsQueue() {
        return new LinkedList<>(entityFieldQueue);
    }

    public String getMappingPath() {
        return mappingPath;
    }

    public List<String> getListFieldPath(boolean includeCollection) {
        List<String> paths = new LinkedList<>();
        if (includeCollection || !dtoField.isCollection()) {
            if (!dtoField.isInnerDTO()) {
                paths.add(mappingPath);
            } else {
                final String finalMappingPath = mappingPath + Constants.DOT;
                paths.addAll(mappingCache.getByDTOClass(dtoField.getClassDeclaring()).getListFieldPath(includeCollection)
                        .stream()
                        .map(finalMappingPath::concat)
                        .distinct()
                        .collect(Collectors.toList()));
            }
        }
        return paths;
    }

    public List<String> getListCollectionFieldPath() {
        List<String> paths = new LinkedList<>();
        if (dtoField.isCollection()) {
            if (!dtoField.isInnerDTO()) {
                paths.add(mappingPath);
            } else {
                final String finalMappingPath = mappingPath + Constants.DOT;
                paths.addAll(mappingCache.getByDTOClass(dtoField.getClassDeclaring()).getListCollectionFieldPath()
                        .stream()
                        .map(finalMappingPath::concat)
                        .distinct()
                        .collect(Collectors.toList()));
            }
        }
        return paths;
    }

    @Override
    public String toString() {
        return "FieldMapping{" +
                "mappingPath='" + mappingPath + '\'' +
                '}';
    }
}
