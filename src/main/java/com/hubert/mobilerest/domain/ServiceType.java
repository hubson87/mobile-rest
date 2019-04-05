package com.hubert.mobilerest.domain;

import com.hubert.mobilerest.exceptions.ValidationFailedException;

public enum ServiceType {
    MOBILE_PREPAID,
    MOBILE_POSTPAID;

    public static ServiceType fromString(String input) {
        try {
            return input == null ? null : valueOf(input);
        } catch (IllegalArgumentException e) {
            throw new ValidationFailedException("Cannot parse value " + input + ". MOBILE_PREPAID and MOBILE_POSTPAID allowed");
        }
    }
}
