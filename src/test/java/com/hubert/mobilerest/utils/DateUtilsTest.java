package com.hubert.mobilerest.utils;

import com.hubert.mobilerest.utils.DateUtils;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {

    @Test
    void shouldMapLocalDateTimeFromEpoch() {
        //given
        long epoch = 1554308106460L;

        //when
        LocalDateTime res = DateUtils.localDateTimeFromEpoch(epoch);

        //then
        assertNotNull(res);
        assertEquals(LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.systemDefault()), res);
    }

    @Test
    void shouldMapEpochFromLocalDateTime() {
        //given
        LocalDateTime localDateTime = LocalDateTime.now();

        //when
        Long epoch = DateUtils.epochFromLocalDateTime(localDateTime);

        //then
        assertNotNull(epoch);
        assertEquals(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), epoch);
    }
}
