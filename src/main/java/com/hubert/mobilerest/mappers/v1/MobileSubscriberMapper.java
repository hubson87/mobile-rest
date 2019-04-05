package com.hubert.mobilerest.mappers.v1;

import com.hubert.mobilerest.domain.MobileSubscriber;
import com.hubert.mobilerest.domain.ServiceType;
import com.hubert.mobilerest.dto.v1.MobileSubscriberDto;
import com.hubert.mobilerest.utils.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;

/**
 * Mapper between MobileSubscriber transfer and model objects
 */
@Mapper
public interface MobileSubscriberMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "serviceStartDate", target = "serviceStartDate", qualifiedByName = "localDateTimeToEpoch")
    MobileSubscriberDto domainToDto(MobileSubscriber mobileSubscriber);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "ownerId", target = "owner.id")
    @Mapping(source = "serviceType", target = "serviceType", qualifiedByName = "parseServiceType")
    @Mapping(source = "serviceStartDate", target = "serviceStartDate", qualifiedByName = "epochToLocalDateTime")
    MobileSubscriber dtoToDomain(MobileSubscriberDto mobileSubscriberDto);

    @Named("parseServiceType")
    default ServiceType parseServiceType(String type) {
        return ServiceType.fromString(type);
    }

    @Named("localDateTimeToEpoch")
    default Long mapLocalDateTimeToEpoch(LocalDateTime localDateTime) {
        return DateUtils.epochFromLocalDateTime(localDateTime);
    }

    @Named("epochToLocalDateTime")
    default LocalDateTime mapEpochToLocalDateTime(Long epochMillis) {
        return DateUtils.localDateTimeFromEpoch(epochMillis);
    }
}
