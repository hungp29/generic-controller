package org.example.genericcontroller.support.generic.mapping;

import lombok.Getter;
import org.springframework.util.Assert;

import javax.persistence.criteria.Path;
import java.lang.reflect.Field;

/**
 * Where Condition.
 *
 * @author hungp
 */
@Getter
public class WhereCondition {

    private FieldMapping fieldMapping;
    private Path<?> path;

    /**
     * New {@link WhereCondition} instance
     *
     * @param fieldMapping {@link FieldMapping} contain DTO, Entity field
     * @param path         {@link Path} instance
     */
    private WhereCondition(FieldMapping fieldMapping, Path<?> path) {
        Assert.notNull(fieldMapping, "Field Mapping must be not null!");
        Assert.notNull(path, "Path must be not null!");
        this.fieldMapping = fieldMapping;
        this.path = path;
    }

    /**
     * Static method to new {@link WhereCondition} instance
     *
     * @param fieldMapping {@link FieldMapping} contain DTO, Entity field
     * @param path         {@link Path} instance
     */
    public static WhereCondition of(FieldMapping fieldMapping, Path<?> path) {
        return new WhereCondition(fieldMapping, path);
    }

    /**
     * Checking field is id or not.
     *
     * @return true if field is id
     */
    public boolean isId() {
        return fieldMapping.isId();
    }

    /**
     * Get class of field converter.
     *
     * @return class
     */
    public Class<?> getConverterType() {
        return fieldMapping.getConverterType();
    }

    /**
     * Get entity field.
     *
     * @return {@link Field} of entity
     */
    public Field getEntityField() {
        return fieldMapping.getLastEntityField().getField();
    }
}
