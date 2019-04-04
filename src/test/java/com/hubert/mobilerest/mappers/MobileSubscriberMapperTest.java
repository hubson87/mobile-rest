package com.hubert.mobilerest.mappers;

import com.hubert.mobilerest.domain.Company;
import com.hubert.mobilerest.domain.Customer;
import com.hubert.mobilerest.domain.MobileSubscriber;
import com.hubert.mobilerest.domain.Person;
import com.hubert.mobilerest.domain.ServiceType;
import com.hubert.mobilerest.dto.v1.MobileSubscriberDto;
import com.hubert.mobilerest.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class MobileSubscriberMapperTest {

    private MobileSubscriberMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(MobileSubscriberMapper.class);
    }

    @Test
    void shouldMapLocalDateTimeToEpochTest() {
        //given
        LocalDateTime localDateTime = LocalDateTime.now();

        //when
        Long epochMillis = mapper.mapLocalDateTimeToEpoch(localDateTime);

        //then
        assertThat(epochMillis, notNullValue());
        assertThat(epochMillis, is(DateUtils.epochFromLocalDateTime(localDateTime)));
    }

    @Test
    void shouldMapEpochToLocalDateTimeTest() {
        //given
        Long epoch = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        //when
        LocalDateTime localDateTime = mapper.mapEpochToLocalDateTime(epoch);

        //then
        assertThat(localDateTime, notNullValue());
        assertThat(localDateTime, is(DateUtils.localDateTimeFromEpoch(epoch)));
    }

    @Test
    void shouldMapEmptyLocalDateTimeToEpochTest() {
        //when
        Long epochMillis = mapper.mapLocalDateTimeToEpoch(null);

        //then
        assertThat(epochMillis, nullValue());
    }

    @Test
    void shouldMapEmptyEpochToLocalDateTimeTest() {
        //when
        LocalDateTime localDateTime = mapper.mapEpochToLocalDateTime(null);

        //then
        assertThat(localDateTime, nullValue());
    }

    @Test
    void shouldMapDtoToDomainTest() {
        //given
        MobileSubscriberDto dto = MobileSubscriberDto.builder()
                .msisdn("48500654321")
                .ownerId(333L)
                .userId(444L)
                .serviceStartDate(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .serviceType("MOBILE_PREPAID")
                .build();
        Customer owner = Company.builder().id(dto.getOwnerId()).build();
        Customer user = Person.builder().id(dto.getUserId()).build();

        //when
        MobileSubscriber res = mapper.dtoToDomain(dto, owner, user);

        assertThat(res, notNullValue());
        assertThat(res.getOwner(), notNullValue());
        assertThat(res.getUser(), notNullValue());
        assertThat(res.getServiceType(), notNullValue());
        assertThat(res.getId(), nullValue());
        assertThat(res.getMsisdn(), is(dto.getMsisdn()));
        assertThat(res.getServiceType().name(), is(dto.getServiceType()));
        assertThat(res.getOwner().getId(), is(dto.getOwnerId()));
        assertThat(res.getUser().getId(), is(dto.getUserId()));
        assertThat(res.getServiceStartDate(), is(DateUtils.localDateTimeFromEpoch(dto.getServiceStartDate())));
    }

    @Test
    void shouldMapDomainToDtoTest() {
        //given
        MobileSubscriber domainObj = MobileSubscriber.builder()
                .msisdn("48500123456")
                .owner(Person.builder().id(123L).build())
                .user(Company.builder().id(977L).build())
                .serviceStartDate(LocalDateTime.now())
                .serviceType(ServiceType.MOBILE_POSTPAID)
                .build();

        //when
        MobileSubscriberDto res = mapper.domainToDto(domainObj);

        //then
        assertThat(res, notNullValue());
        assertThat(res.getMsisdn(), is(domainObj.getMsisdn()));
        assertThat(res.getOwnerId(), is(domainObj.getOwner().getId()));
        assertThat(res.getUserId(), is(domainObj.getUser().getId()));
        assertThat(res.getServiceStartDate(), is(DateUtils.epochFromLocalDateTime(domainObj.getServiceStartDate())));
        assertThat(res.getServiceType(), is(domainObj.getServiceType().name()));
        assertThat(res.getUserId(), is(domainObj.getUser().getId()));
    }

    @Test
    void shouldMapEmptyDtoToDomainTest() {
        //given
        MobileSubscriberDto subscriberDto = MobileSubscriberDto.builder().build();

        //when
        MobileSubscriber res = mapper.dtoToDomain(subscriberDto, null, null);

        //then
        assertThat(res, notNullValue());
        assertThat(res.getId(), nullValue());
        assertThat(res.getMsisdn(), nullValue());
        assertThat(res.getServiceType(), nullValue());
        assertThat(res.getOwner(), nullValue());
        assertThat(res.getUser(), nullValue());
        assertThat(res.getServiceStartDate(), nullValue());
    }

    @Test
    void shouldMapEmptyDomainToDtoTest() {
        //given
        MobileSubscriber domainObj = MobileSubscriber.builder().build();

        //when
        MobileSubscriberDto res = mapper.domainToDto(domainObj);

        //then
        assertThat(res, notNullValue());
        assertThat(res.getMsisdn(), nullValue());
        assertThat(res.getOwnerId(), nullValue());
        assertThat(res.getServiceStartDate(), nullValue());
        assertThat(res.getServiceType(), nullValue());
        assertThat(res.getUserId(), nullValue());
    }


}
