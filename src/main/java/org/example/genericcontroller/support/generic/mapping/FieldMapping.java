package org.example.genericcontroller.support.generic.mapping;

import org.example.genericcontroller.support.generic.FilterData;
import org.example.genericcontroller.support.generic.MappingField;
import org.example.genericcontroller.support.generic.ObjectMappingCache;
import org.example.genericcontroller.support.generic.exception.GenericException;
import org.example.genericcontroller.support.generic.mapping.ObjectMapping.SelectionType;
import org.example.genericcontroller.support.generic.utils.DuplicateChecker;
import org.example.genericcontroller.utils.ObjectUtils;
import org.example.genericcontroller.utils.constant.Constants;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

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

    private ObjectMapping objectMapping;
    private GenericField dtoField;
    private LinkedList<GenericField> entityFieldQueue;
    private String mappingPath;
    private boolean isId;
    private ObjectMappingCache mappingCache;
    private Class<?> converterType;

    /**
     * New instance of {@link FieldMapping}.
     *
     * @param dtoField      {@link Field} of DTO
     * @param entityFields  {@link Field} of entity
     * @param mappingPath   mapping class
     * @param objectMapping {@link ObjectMapping} parent contains {@link FieldMapping}
     * @param mappingCache  {@link ObjectMappingCache} object mapping cache
     */
    private FieldMapping(Field dtoField, List<Field> entityFields, String mappingPath, ObjectMapping objectMapping, ObjectMappingCache mappingCache) {
        Assert.notNull(dtoField, "Data Transfer Object field must be not null");
        Assert.notEmpty(entityFields, "Entity field must be not empty");
        Assert.notNull(objectMapping, "Object Mapping must be not null");
        this.mappingPath = mappingPath;
        this.mappingCache = mappingCache;
        this.objectMapping = objectMapping;
        this.dtoField = GenericField.of(dtoField, null, mappingCache);
        this.entityFieldQueue = new LinkedList<>();
        GenericField parentField = null;
        for (Field entityField : entityFields) {
            this.entityFieldQueue.add(GenericField.of(entityField, parentField, mappingCache));
            parentField = this.entityFieldQueue.getLast();
        }
        this.isId = AnnotatedElementUtils.hasAnnotation(entityFieldQueue.getLast().getField(), Id.class);

        MappingField mappingField = ObjectUtils.getAnnotation(dtoField, MappingField.class);
        if (null != mappingField) {
            this.converterType = mappingField.converter();
        }
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
     * @param mappingPath   mapping path
     * @param objectMapping {@link ObjectMapping} parent contains {@link FieldMapping}
     * @param mappingCache  {@link ObjectMappingCache} object mapping cache
     * @param dtoField      {@link Field} DTO Field
     * @param entityField   {@link Field} Entity Field
     * @return {@link FieldMapping} instance
     */
    public static FieldMapping of(String mappingPath, ObjectMapping objectMapping, ObjectMappingCache mappingCache, Field dtoField, Field... entityField) {
        return new FieldMapping(dtoField, Arrays.asList(entityField), mappingPath, objectMapping, mappingCache);
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
     * Get {@link ObjectMapping} parent of mapping field.
     *
     * @return {@link ObjectMapping} instance
     */
    public ObjectMapping getObjectMapping() {
        return objectMapping;
    }

    /**
     * Get {@link ObjectMappingCache} instance.
     *
     * @return {@link ObjectMappingCache}
     */
    public ObjectMappingCache getMappingCache() {
        return mappingCache;
    }

    /**
     * Get Field Converter type.
     *
     * @return Field Converter type
     */
    public Class<?> getConverterType() {
        return converterType;
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
     * Checking field is inner object (DTO, Entity).
     *
     * @return true if field is inner object
     */
    public boolean isInnerObject() {
        return dtoField.isInnerObject() || entityFieldQueue.getLast().isInnerObject();
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

    /**
     * Build {@link Selection} for {@link FieldMapping}.
     *
     * @param from          {@link From} instance
     * @param selectionType {@link SelectionType} selection type
     * @param prefixPath    prefix path of field
     * @return list {@link Selection}
     */
    List<Selection<?>> getSelections(From<?, ?> from, SelectionType selectionType, FilterData filterData, PrefixPath prefixPath) {
        List<Selection<?>> selections = new ArrayList<>();
        if (isId() || (isKeepField(prefixPath, filterData) &&
                (SelectionType.ALL_FIELD.equals(selectionType) ||
                        (SelectionType.COLLECTION_FIELD.equals(selectionType) && isInnerObject()) ||
                        (SelectionType.NONE_COLLECTION_FIELD.equals(selectionType) && !isCollection())))) {

            String prefixAlias = null == prefixPath ? "" : prefixPath.getEntityPrefix();
            int index = 0;
            for (GenericField entityFieldElement : entityFieldQueue) {
                prefixAlias += entityFieldElement.getFieldName();
                index++;
                if (index == entityFieldQueue.size()) {
                    if (!entityFieldElement.isInnerEntity()) {
                        selections.add(from.get(entityFieldElement.getFieldName()).alias(prefixAlias));
                    } else {
                        // If FieldMapping is inner Entity object
                        ObjectMapping innerObj = mappingCache.getByDTOClass(dtoField.getFieldClass());
                        if (null != innerObj) {
                            // If selection type is COLLECTION_FIELD and field mapping is inner entity then change selection type
                            if (SelectionType.COLLECTION_FIELD.equals(selectionType)) {
                                // If field is collection then selection type is ALL_FIELD, otherwise ID_FIELD to get only id of inner entity
                                selectionType = isCollection() ? SelectionType.ALL_FIELD : SelectionType.ID_FIELD;
                            }
                            selections.addAll(innerObj.getSelections(
                                    getJoin(from, entityFieldElement),
                                    selectionType,
                                    filterData,
                                    new PrefixPath.PrefixPathBuilder(prefixPath).add(this).build()));
                        }
                    }
                } else {
                    from = getJoin(from, entityFieldElement);
                    prefixAlias += Constants.DOT;
                }
            }
        }
        return selections;
    }

    From<?, ?> buildFrom(From<?, ?> from) {
        int lastIndex = entityFieldQueue.size();
        if (!isInnerObject()) {
            --lastIndex;
        }
        for (int i = 0; i < lastIndex; i++) {
            GenericField entityFieldElement = entityFieldQueue.get(i);
            from = getJoin(from, entityFieldElement);
        }
        return from;
    }

    /**
     * Checking field is keeping or remove.
     *
     * @param prefixPath prefix path of field
     * @param filterData {@link FilterData} field data
     * @return true if field is keep
     */
    private boolean isKeepField(PrefixPath prefixPath, FilterData filterData) {
        String fieldPath = (null == prefixPath ? "" : prefixPath.getDtoPrefix()) + getDTOField().getFieldName();
        boolean keep = isId() || (isInnerObject() && !isCollection()) || (!StringUtils.isEmpty(fieldPath) && null == filterData.getFilter());
        if (!keep && null != filterData.getFilter()) {
            for (String keepField : filterData.getFilter()) {
                if (fieldPath.equals(keepField) ||
                        fieldPath.startsWith(keepField.concat(Constants.DOT)) ||
                        keepField.startsWith(fieldPath.concat(Constants.DOT))) {
                    keep = true;
                    break;
                }
            }
        }
        return keep;
    }

    /**
     * Get {@link Join} instance, if {@link Join} don't exist then create new LEFT JOIN.
     *
     * @param from        {@link From} instance
     * @param entityField {@link GenericField} entity field
     * @return {@link Join} instance
     */
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
