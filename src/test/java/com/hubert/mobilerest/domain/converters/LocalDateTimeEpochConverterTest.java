package com.hubert.mobilerest.domain.converters;

import com.hubert.mobilerest.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class LocalDateTimeEpochConverterTest {
    private LocalDateTimeEpochConverter converter;

    @BeforeEach
    public void setUp() {
        converter = new LocalDateTimeEpochConverter();
    }

    @Test
    void shouldConvertToDatabaseColumnTest() {
        //given
        LocalDateTime localDateTime = LocalDateTime.now();

        //when
        Long epochMillis = converter.convertToDatabaseColumn(localDateTime);

        //then
        assertThat(epochMillis, notNullValue());
        assertThat(epochMillis, is(DateUtils.epochFromLocalDateTime(localDateTime)));
    }

    @Test
    void shouldConvertNullToDatabaseColumnTest() {//when
        Long epochMillis = converter.convertToDatabaseColumn(null);

        //then
        assertThat(epochMillis, nullValue());
    }

    @Test
    void shouldConvertToEntityAttributeTest() {
        //given
        Long epoch = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        //when
        LocalDateTime localDateTime = converter.convertToEntityAttribute(epoch);

        //then
        assertThat(localDateTime, notNullValue());
        assertThat(localDateTime, is(DateUtils.localDateTimeFromEpoch(epoch)));
    }

    @Test
    void shouldConvertNullToEntityAttributeTest() {
        //when
        LocalDateTime localDateTime = converter.convertToEntityAttribute(null);

        //then
        assertThat(localDateTime, nullValue());
    }
}
