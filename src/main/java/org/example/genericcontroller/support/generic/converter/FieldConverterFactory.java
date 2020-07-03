package org.example.genericcontroller.support.generic.converter;

import org.example.genericcontroller.utils.SpringContext;

/**
 * Field Converter Factory.
 *
 * @author hungp
 */
public class FieldConverterFactory {

    private FieldConverterFactory() {
    }

    @SuppressWarnings("rawtypes")
    public static <T> FieldConverter getFieldConverter(Class<T> converterType) {
        if (null != converterType && FieldConverter.class.isAssignableFrom(converterType)) {
            return (FieldConverter) SpringContext.getBean(converterType);
        }
        return null;
    }

}
