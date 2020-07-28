package org.example.genericcontroller.support.generic.mapping;

import lombok.Getter;

import javax.persistence.criteria.Path;

/**
 * Where Condition.
 *
 * @author hungp
 */
@Getter
public class WhereCondition {

    private FieldMapping fieldMapping;
    private Path<?> path;

    private WhereCondition(FieldMapping fieldMapping, Path<?> path) {
        this.fieldMapping = fieldMapping;
        this.path = path;
    }

    public static WhereCondition of(FieldMapping fieldMapping, Path<?> path) {
        return new WhereCondition(fieldMapping, path);
    }
}
