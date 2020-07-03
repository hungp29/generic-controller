package org.example.genericcontroller.support.generic.converter;

import org.example.genericcontroller.utils.DateTimeUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Local Date Time Converter.
 *
 * @author hungp
 */
@Component
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
