package org.example.genericcontroller.support.generic.fieldconverter;

import org.example.genericcontroller.support.generic.FieldConverter;
import org.example.genericcontroller.utils.DateTimeUtils;

import java.time.LocalDateTime;

/**
 * Local Date Time Converter.
 *
 * @author hungp
 */
public class LocalDateTimeConverter implements FieldConverter<LocalDateTime, Long> {

    @Override
    public Long convertToFieldDataTransferObject(LocalDateTime entityField) {
        return DateTimeUtils.convertLocalDateTimeToMilli(entityField);
    }

    @Override
    public LocalDateTime convertToFieldEntity(Long dtoField) {
        return DateTimeUtils.convertMilliToLocalDateTime(dtoField);
    }
}
