package org.example.genericcontroller.support.generic.mapping;

import org.example.genericcontroller.support.generic.ObjectMappingCache;
import org.example.genericcontroller.support.generic.exception.GenericException;
import org.example.genericcontroller.support.generic.utils.DuplicateChecker;
import org.example.genericcontroller.utils.constant.Constants;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;

import javax.persistence.Id;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Selection;
import java.lang.reflect.Field;
import java.util.ArrayList;
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
    private boolean isId;
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
        this.isId = AnnotatedElementUtils.hasAnnotation(entityFieldQueue.getLast().getField(), Id.class);
        validate();
    }

    /**
     * Validate data.
     */
    private void validate() {
        if ((entityFieldQueue.getLast().isCollection() && !dtoField.isCollection()) ||
                (!entityFieldQueue.getLast().isCollection() && dtoField.isCollection())) {
            throw new GenericException("DTO Field " + dtoField.getDeclareClassName() + "." + dtoField.getFieldName() +
                    " and Entity Field " + entityFieldQueue.getLast().getDeclareClassName() + "." +
                    entityFieldQueue.getLast().getFieldName() + " must be collection");
        }
        if ((entityFieldQueue.getLast().isInnerObject() && !dtoField.isInnerObject()) ||
                (!entityFieldQueue.getLast().isInnerObject() && dtoField.isInnerObject())) {
            throw new GenericException("DTO Field " + dtoField.getDeclareClassName() + "." + dtoField.getFieldName() +
                    " and Entity Field " + entityFieldQueue.getLast().getDeclareClassName() + "." +
                    entityFieldQueue.getLast().getFieldName() + " must be Inner Object (Entity, DTO)");
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

    /**
     * Get last entity field in path.
     * Ex: user.address.id -> get field id of address of user
     *
     * @return entity field
     */
    public GenericField getLastEntityField() {
        return entityFieldQueue.getLast();
    }

    /**
     * Get entity field as queue.
     *
     * @return entity field queue
     */
    public Queue<GenericField> getEntityFieldAsQueue() {
        return new LinkedList<>(entityFieldQueue);
    }

    /**
     * Checking entity path having one element or not.
     *
     * @return true if entity path having one element
     */
    public boolean isOneElementOfEntityPath() {
        return entityFieldQueue.size() == 1;
    }

    /**
     * Get mapping path.
     *
     * @return mapping path
     */
    public String getMappingPath() {
        return mappingPath;
    }

    /**
     * Check field is collection.
     *
     * @return true if field is collection
     */
    public boolean isCollection() {
        return dtoField.isCollection();
    }

    /**
     * Checking field is Id or not.
     *
     * @return true if field is Id;
     */
    public boolean isId() {
        return isId;
    }

    /**
     * Get list field path of field.
     *
     * @param includeCollection flat to detect get path of collection field
     * @return list field path
     */
    public List<String> getListFieldPath(boolean includeCollection) {
        List<String> paths = new LinkedList<>();
        if (includeCollection || !dtoField.isCollection()) {
            if (!dtoField.isInnerDTO()) {
                paths.add(mappingPath);
            } else {
                final String finalMappingPath = mappingPath + Constants.DOT;
                paths.addAll(mappingCache.getByDTOClass(dtoField.getFieldClass()).getListFieldPath(includeCollection)
                        .stream()
                        .map(finalMappingPath::concat)
                        .distinct()
                        .collect(Collectors.toList()));
            }
        }
        return paths;
    }

    public List<Selection<?>> getSelections(From<?, ?> from, ObjectMapping.SelectionType selectionType, String prefixAlias) {
        List<Selection<?>> selections = new ArrayList<>();
        int index = 0;
        for (GenericField entityFieldElement : entityFieldQueue) {
            prefixAlias += entityFieldElement.getFieldName();
            index++;
            if (index == entityFieldQueue.size()) {
                if (!entityFieldElement.isInnerEntity()) {
                    selections.add(from.get(entityFieldElement.getFieldName()).alias(prefixAlias));
                } else {
                    ObjectMapping innerObj = mappingCache.getByDTOClass(dtoField.getFieldClass());
                    if (null != innerObj) {
                        selections.addAll(innerObj.getSelections(getJoin(from, entityFieldElement), selectionType, prefixAlias + Constants.DOT));
                    }
                }
            } else {
                from = getJoin(from, entityFieldElement);
                prefixAlias += Constants.DOT;
            }
        }
        return selections;
    }

    private Join<?, ?> getJoin(From<?, ?> from, GenericField entityField) {
        Join<?, ?> join = DuplicateChecker.existJoin(from, entityField.getFieldClass());
        if (null == join) {
            join = from.join(entityField.getFieldName(), JoinType.LEFT);
        }
        return join;
    }

    @Override
    public String toString() {
        return "FieldMapping{" +
                "mappingPath='" + mappingPath + '\'' +
                '}';
    }
}
