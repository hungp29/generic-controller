package org.example.genericcontroller.support.generic.obj;

import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

/**
 * DTO Field.
 *
 * @author hungp
 */
public class DTOField extends GenericField {

    private static final String FIELD_MUST_BE_NOT_NULL = "Field must be not null";

    /**
     * Prevent new instance.
     */
    private DTOField(Field field) {
        this.field = field;
    }

    /**
     * Create new instance of {@link DTOField} from {@link Field}.
     *
     * @param field {@link Field} instance
     * @return {@link DTOField} instance
     */
    public static DTOField of(Field field) {
        return new DTOField(field);
    }

    @Override
    public List<String> getMappingFieldPath(boolean lookingInner, boolean includeCollection) {
        Assert.notNull(field, FIELD_MUST_BE_NOT_NULL);
        return Collections.singletonList(getMappingFieldName());
    }
}
