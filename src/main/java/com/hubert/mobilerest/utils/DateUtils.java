package com.hubert.mobilerest.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Utility class related to dates
 */
public class DateUtils {
    private DateUtils() {

    }

    /**
     * Mapping between epoch in millis to LocalDateTime
     * @param epochMillis Input epoch in milliseconds
     * @return LocalDateTime parsed from epoch using default system timezone
     */
    public static LocalDateTime localDateTimeFromEpoch(Long epochMillis) {
        return epochMillis == null ? null
                :  LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault());
    }

    /**
     * Mapping between LocalDateTime and epoch in millis
     * @param localDateTime Input local date time
     * @return Epoch in milliseconds mapped using system default timezone
     */
    public static Long epochFromLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime == null ? null
                :  localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
