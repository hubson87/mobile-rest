package com.hubert.mobilerest.domain.converters;

import com.hubert.mobilerest.utils.DateUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.LocalDateTime;

/**
 * Converter for database epoch in milliseconds and LocalDateTime used in java
 */
@Converter
public class LocalDateTimeEpochConverter implements AttributeConverter<LocalDateTime, Long> {

    @Override
    public Long convertToDatabaseColumn(LocalDateTime localDateTime) {
        return DateUtils.epochFromLocalDateTime(localDateTime);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Long epochMillis) {
        return DateUtils.localDateTimeFromEpoch(epochMillis);
    }
}
