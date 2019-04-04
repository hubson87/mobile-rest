package com.hubert.mobilerest.mappers;

import com.hubert.mobilerest.domain.Customer;
import com.hubert.mobilerest.domain.MobileSubscriber;
import com.hubert.mobilerest.dto.v1.MobileSubscriberDto;
import com.hubert.mobilerest.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
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
    @Mapping(source = "user", target = "user")
    @Mapping(source = "owner", target = "owner")
    @Mapping(source = "mobileSubscriberDto.serviceStartDate", target = "serviceStartDate", qualifiedByName = "epochToLocalDateTime")
    MobileSubscriber dtoToDomain(MobileSubscriberDto mobileSubscriberDto, Customer owner, Customer user);

    @Named("localDateTimeToEpoch")
    default Long mapLocalDateTimeToEpoch(LocalDateTime localDateTime) {
        return DateUtils.epochFromLocalDateTime(localDateTime);
    }

    @Named("epochToLocalDateTime")
    default LocalDateTime mapEpochToLocalDateTime(Long epochMillis) {
        return DateUtils.localDateTimeFromEpoch(epochMillis);
    }
}
