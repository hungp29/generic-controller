package org.example.genericcontroller.support.generic.obj;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

public class DTOField extends GenericField {

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
    public List<String> getMappingFieldPath() {
        return Collections.singletonList(getMappingFieldName());
    }
}
