package org.example.genericcontroller.support.generic.dto;

import java.lang.reflect.Field;

/**
 * Entity Field.
 */
public class EntityField extends GenericField {

    /**
     * Prevent new instance.
     *
     * @param field {@link Field} instance
     */
    private EntityField(Field field) {
        this.field = field;
    }

    /**
     * New instance of {@link EntityField} from {@link Field}.
     *
     * @param field {@link Field} instance
     * @return {@link EntityField} instance
     */
    public static EntityField of(Field field) {
        return new EntityField(field);
    }
}
