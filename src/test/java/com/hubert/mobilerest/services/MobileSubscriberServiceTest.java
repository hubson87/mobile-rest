package com.hubert.mobilerest.services;

import com.hubert.mobilerest.domain.Company;
import com.hubert.mobilerest.domain.Customer;
import com.hubert.mobilerest.domain.MobileSubscriber;
import com.hubert.mobilerest.domain.Person;
import com.hubert.mobilerest.domain.ServiceType;
import com.hubert.mobilerest.dto.v1.MobileSubscriberDto;
import com.hubert.mobilerest.dto.v1.MobileSubscribersDto;
import com.hubert.mobilerest.exceptions.ResourceNotFoundException;
import com.hubert.mobilerest.exceptions.ValidationFailedException;
import com.hubert.mobilerest.mappers.MobileSubscriberMapper;
import com.hubert.mobilerest.repositories.CustomerRepository;
import com.hubert.mobilerest.repositories.MobileSubscriberRepository;
import com.hubert.mobilerest.utils.DateUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MobileSubscriberServiceTest {

    @Mock
    private MobileSubscriberRepository subscriberRepository;

    @Mock
    private CustomerRepository customerRepository;

    private MobileSubscriberService service;

    private Customer person;
    private Customer company;
    private MobileSubscriber subscriber;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        MobileSubscriberMapper mapper = Mappers.getMapper(MobileSubscriberMapper.class);
        service = new MobileSubscriberServiceImpl(subscriberRepository, customerRepository, mapper);

        person = Person.builder().id(1L).address("SomeAddr").docId("123123").firstName("aaa").lastName("ddd").build();
        company = Company.builder().id(2L).address("SomeAddr2").taxId("223123").companyName("sssaaa").build();
        subscriber = MobileSubscriber.builder().id(1L).user(person).owner(company).msisdn("48123312123")
                .serviceType(ServiceType.MOBILE_POSTPAID).serviceStartDate(LocalDateTime.now().withNano(123000000)).build();
    }

    @Test
    void shouldNotFindSubscriberByIdTest() {
        //given
        when(subscriberRepository.findById(1L)).thenReturn(Optional.empty());

        //when/then
        assertThrows(ResourceNotFoundException.class, () -> service.findSubscriberById(1L));
    }

    @Test
    void shouldFindSubscriberByIdTest() {
        //given
        when(subscriberRepository.findById(subscriber.getId())).thenReturn(Optional.of(subscriber));

        //when
        MobileSubscriberDto subscriber = service.findSubscriberById(this.subscriber.getId());

        //then
        assertThat(subscriber, notNullValue());
        assertThat(subscriber.getMsisdn(), is(this.subscriber.getMsisdn()));
        assertThat(subscriber.getOwnerId(), is(this.subscriber.getOwnerId()));
        assertThat(subscriber.getUserId(), is(this.subscriber.getUserId()));
    }

    @Test
    void shouldFindAllSubscribersTest() {
        //given
        when(subscriberRepository.findAll()).thenReturn(Lists.newArrayList(subscriber, MobileSubscriber.builder().id(2L).build()));

        //when
        MobileSubscribersDto foundSubscribers = service.findSubscribersByCriteria(null);

        //then
        assertThat(foundSubscribers.getSubscribers(), hasSize(2));
    }

    @Test
    void shouldFindSubscribersByPartialCriteriaTest() {
        //given
        MobileSubscriberDto criteria =
                MobileSubscriberDto.builder().msisdn(subscriber.getMsisdn()).build();
        when(subscriberRepository.findByCriteria(any())).thenReturn(Collections.singletonList(subscriber));

        //when
        MobileSubscribersDto res = service.findSubscribersByCriteria(criteria);

        //then
        assertThat(res.getSubscribers(), hasSize(1));
        assertThat(res.getSubscribers().get(0).getMsisdn(), is(criteria.getMsisdn()));
    }

    @Test
    void shouldFindSubscribersByFullCriteriaTest() {
        //given
        MobileSubscriberDto criteria =
                MobileSubscriberDto.builder().msisdn(subscriber.getMsisdn()).userId(subscriber.getUserId()).ownerId(subscriber.getOwnerId())
                        .serviceStartDate(DateUtils.epochFromLocalDateTime(subscriber.getServiceStartDate()))
                        .serviceType(subscriber.getServiceType().name()).build();
        when(subscriberRepository.findByCriteria(any())).thenReturn(Collections.singletonList(subscriber));

        //when
        MobileSubscribersDto res = service.findSubscribersByCriteria(criteria);

        //then
        assertThat(res.getSubscribers(), hasSize(1));
        assertThat(res.getSubscribers().get(0).getMsisdn(), is(criteria.getMsisdn()));
        assertThat(res.getSubscribers().get(0).getOwnerId(), is(criteria.getOwnerId()));
        assertThat(res.getSubscribers().get(0).getUserId(), is(criteria.getUserId()));
    }

    @Test
    void shouldNotCreateNewSubscriberAndThrowExceptionWhenMsisdnExistsTest() {
        //given
        String msdnid = "12321";
        when(subscriberRepository.findFirstByMsisdn(anyString())).thenReturn(Optional.of(MobileSubscriber.builder().build()));
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(Person.builder().build()));

        //when/then
        assertThrows(ValidationFailedException.class, () -> service.createNewSubscriber(MobileSubscriberDto.builder().msisdn(msdnid).build()));
        verify(subscriberRepository, times(1)).findFirstByMsisdn(msdnid);
        verify(customerRepository, never()).findById(any());
    }

    @Test
    void shouldNotCreateNewSubscriberAndThrowExceptionWhenOwnerNotExistsTest() {
        //given
        String msdnid = "12321";
        Long ownerId = 1L, userId = 2L;
        MobileSubscriberDto newObj = MobileSubscriberDto.builder().msisdn(msdnid).userId(userId).ownerId(ownerId).build();

        when(subscriberRepository.findFirstByMsisdn(anyString())).thenReturn(Optional.empty());
        when(customerRepository.findById(ownerId)).thenReturn(Optional.empty());
        when(customerRepository.findById(userId)).thenReturn(Optional.of(Person.builder().build()));

        //when/then
        assertThrows(ResourceNotFoundException.class, () -> service.createNewSubscriber(newObj));

        verify(subscriberRepository, times(1)).findFirstByMsisdn(msdnid);
        verify(customerRepository, times(1)).findById(ownerId);
    }

    @Test
    void shouldNotCreateNewSubscriberAndThrowExceptionWhenUserNotExistsTest() {
        //given
        String msdnid = "12321";
        Long ownerId = 1L, userId = 2L;
        MobileSubscriberDto newObj = MobileSubscriberDto.builder().msisdn(msdnid).userId(userId).ownerId(ownerId).build();

        when(subscriberRepository.findFirstByMsisdn(anyString())).thenReturn(Optional.empty());

        when(customerRepository.findById(userId)).thenReturn(Optional.empty());
        when(customerRepository.findById(ownerId)).thenReturn(Optional.of(Person.builder().build()));

        //when/then
        assertThrows(ResourceNotFoundException.class, () -> service.createNewSubscriber(newObj));
        verify(subscriberRepository, times(1)).findFirstByMsisdn(msdnid);
        verify(customerRepository, times(1)).findById(userId);
    }

    @Test
    void shouldCreateNewSubscriberTest() {
        //given
        MobileSubscriberDto newObj =
                MobileSubscriberDto.builder().msisdn(subscriber.getMsisdn()).userId(subscriber.getUserId()).ownerId(subscriber.getOwnerId())
                        .serviceType(subscriber.getServiceType().name()).build();

        when(subscriberRepository.findFirstByMsisdn(anyString())).thenReturn(Optional.empty());
        when(customerRepository.findById(newObj.getOwnerId())).thenReturn(Optional.of(company));
        when(customerRepository.findById(newObj.getUserId())).thenReturn(Optional.of(person));
        when(subscriberRepository.save(any())).thenReturn(subscriber);

        //when
        MobileSubscriberDto saved = service.createNewSubscriber(newObj);

        //then
        assertThat(saved, notNullValue());
        assertThat(saved.getMsisdn(), is(newObj.getMsisdn()));
        assertThat(saved.getUserId(), is(newObj.getUserId()));
        assertThat(saved.getOwnerId(), is(newObj.getOwnerId()));
        assertThat(saved.getServiceType(), is(newObj.getServiceType()));
    }

    @Test
    void shouldNotUpdateSubscriberAndThrowExceptionWhenOwnerNotExistsTest() {
        //given
        String msdnid = subscriber.getMsisdn();
        Long ownerId = 5L, userId = subscriber.getUserId();
        MobileSubscriberDto newObj = MobileSubscriberDto.builder().msisdn(msdnid).userId(userId).ownerId(ownerId).build();

        when(subscriberRepository.findById(anyLong())).thenReturn(Optional.of(subscriber));
        when(customerRepository.findById(userId)).thenReturn(Optional.of(subscriber.getUser()));
        when(customerRepository.findById(ownerId)).thenReturn(Optional.empty());

        //when/then
        assertThrows(ResourceNotFoundException.class, () -> service.updateSubscriber(newObj, subscriber.getId()));
        verify(customerRepository, times(1)).findById(ownerId);
    }

    @Test
    void shouldNotUpdateSubscriberWhenUserNotExistsTest() {
        //given
        String msdnid = subscriber.getMsisdn();
        Long ownerId = subscriber.getOwnerId(), userId = 10L;
        MobileSubscriberDto newObj = MobileSubscriberDto.builder().msisdn(msdnid).userId(userId).ownerId(ownerId).build();

        when(subscriberRepository.findById(anyLong())).thenReturn(Optional.of(subscriber));
        when(customerRepository.findById(userId)).thenReturn(Optional.empty());
        when(customerRepository.findById(ownerId)).thenReturn(Optional.of(subscriber.getOwner()));

        //when/then
        assertThrows(ResourceNotFoundException.class, () -> service.updateSubscriber(newObj, 10L));
        verify(customerRepository, times(1)).findById(userId);
    }

    @Test
    void shouldNotUpdateSubscriberAndThrowExceptionWhenMsisdnChangedTest() {
        //given
        String msdnid = "12321";
        Long ownerId = subscriber.getOwnerId(), userId = subscriber.getUserId();
        MobileSubscriberDto newObj = MobileSubscriberDto.builder().msisdn(msdnid).userId(userId).ownerId(ownerId)
                .serviceType(subscriber.getServiceType().name())
                .serviceStartDate(DateUtils.epochFromLocalDateTime(subscriber.getServiceStartDate())).build();

        when(subscriberRepository.findById(anyLong())).thenReturn(Optional.of(subscriber));
        when(customerRepository.findById(userId)).thenReturn(Optional.of(subscriber.getUser()));
        when(customerRepository.findById(ownerId)).thenReturn(Optional.of(subscriber.getOwner()));

        //when/then
        assertThrows(ValidationFailedException.class, () -> service.updateSubscriber(newObj, subscriber.getId()));
        verify(customerRepository, times(1)).findById(userId);
        verify(customerRepository, times(1)).findById(ownerId);
        verify(subscriberRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldNotUpdateSubscriberAndThrowExceptionWhenServiceStartDateChangedTest() {
        //given
        Long newDate = DateUtils.epochFromLocalDateTime(subscriber.getServiceStartDate().plusDays(2));
        Long ownerId = subscriber.getOwnerId(), userId = subscriber.getUserId();
        MobileSubscriberDto newObj = MobileSubscriberDto.builder().msisdn(subscriber.getMsisdn()).userId(userId).ownerId(ownerId)
                .serviceType(subscriber.getServiceType().name()).serviceStartDate(newDate).build();

        when(subscriberRepository.findById(anyLong())).thenReturn(Optional.of(subscriber));
        when(customerRepository.findById(userId)).thenReturn(Optional.of(subscriber.getUser()));
        when(customerRepository.findById(ownerId)).thenReturn(Optional.of(subscriber.getOwner()));

        //when/then
        assertThrows(ValidationFailedException.class, () -> service.updateSubscriber(newObj, subscriber.getId()));
        verify(customerRepository, times(1)).findById(userId);
        verify(customerRepository, times(1)).findById(ownerId);
        verify(subscriberRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldNotUpdateSubscriberWhenNothingChangedTest() {
        //given
        Long ownerId = subscriber.getOwnerId(), userId = subscriber.getUserId();
        MobileSubscriberDto newObj = MobileSubscriberDto.builder().msisdn(subscriber.getMsisdn()).userId(userId).ownerId(ownerId)
                .serviceType(subscriber.getServiceType().name())
                .build();

        when(subscriberRepository.findById(anyLong())).thenReturn(Optional.of(subscriber));
        when(customerRepository.findById(userId)).thenReturn(Optional.of(subscriber.getUser()));
        when(customerRepository.findById(ownerId)).thenReturn(Optional.of(subscriber.getOwner()));

        //when
        MobileSubscriberDto updatedCustomer = service.updateSubscriber(newObj, subscriber.getId());

        //then
        assertThat(updatedCustomer, notNullValue());
        verify(customerRepository, times(1)).findById(userId);
        verify(customerRepository, times(1)).findById(ownerId);
        verify(subscriberRepository, times(1)).findById(anyLong());
        verify(subscriberRepository, never()).save(any());
    }

    @Test
    void shouldUpdateSubscriberWhenAllowedDataChangedTest() {
        //given
        Customer newUser = Person.builder().id(99L).build();
        Customer newOwner = Person.builder().id(98L).build();
        String newServiceType = ServiceType.MOBILE_PREPAID.name();

        MobileSubscriberDto newObj = MobileSubscriberDto.builder().msisdn(subscriber.getMsisdn()).userId(newUser.getId())
                .ownerId(newOwner.getId()).serviceType(newServiceType)
                .build();

        when(subscriberRepository.findById(subscriber.getId())).thenReturn(Optional.of(subscriber));
        when(customerRepository.findById(newUser.getId())).thenReturn(Optional.of(newUser));
        when(customerRepository.findById(newOwner.getId())).thenReturn(Optional.of(newOwner));
        when(subscriberRepository.save(any())).thenReturn(MobileSubscriber.builder()
                .owner(newOwner)
                .user(newUser)
                .serviceType(ServiceType.valueOf(newServiceType))
                .build());

        //when
        MobileSubscriberDto updatedCustomer = service.updateSubscriber(newObj, subscriber.getId());

        //then
        assertThat(updatedCustomer, notNullValue());
        assertThat(updatedCustomer.getUserId(), is(newUser.getId()));
        assertThat(updatedCustomer.getOwnerId(), is(newOwner.getId()));
        assertThat(updatedCustomer.getServiceType(), is(newServiceType));

        verify(customerRepository, times(1)).findById(newUser.getId());
        verify(customerRepository, times(1)).findById(newOwner.getId());
        verify(subscriberRepository, times(1)).findById(subscriber.getId());
        verify(subscriberRepository, times(1)).save(any());
    }

    @Test
    void shouldNotPatchSubscriberAndThrowExceptionWhenOwnerNotExistsTest() {
        //given
        Long ownerId = 5L;
        MobileSubscriberDto newObj = MobileSubscriberDto.builder().ownerId(ownerId).build();

        when(subscriberRepository.findById(anyLong())).thenReturn(Optional.of(subscriber));
        when(customerRepository.findById(ownerId)).thenReturn(Optional.empty());

        //when/then
        assertThrows(ResourceNotFoundException.class, () -> service.patchSubscriber(newObj, subscriber.getId()));
        verify(customerRepository, times(1)).findById(ownerId);
    }

    @Test
    void shouldNotPatchSubscriberWhenUserNotExistsTest() {
        //given
        Long userId = 10L;
        MobileSubscriberDto newObj = MobileSubscriberDto.builder().userId(userId).build();

        when(subscriberRepository.findById(anyLong())).thenReturn(Optional.of(subscriber));
        when(customerRepository.findById(userId)).thenReturn(Optional.empty());

        //when/then
        assertThrows(ResourceNotFoundException.class, () -> service.patchSubscriber(newObj, 10L));
        verify(customerRepository, times(1)).findById(userId);
    }

    @Test
    void shouldNotPatchSubscriberAndThrowExceptionWhenMsisdnChangedTest() {
        //given
        String msdnid = "12321";
        MobileSubscriberDto newObj = MobileSubscriberDto.builder().msisdn(msdnid).build();

        when(subscriberRepository.findById(anyLong())).thenReturn(Optional.of(subscriber));

        //when/then
        assertThrows(ValidationFailedException.class, () -> service.patchSubscriber(newObj, subscriber.getId()));
        verify(subscriberRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldNotPatchSubscriberAndThrowExceptionWhenServiceStartDateChangedTest() {
        //given
        Long newDate = DateUtils.epochFromLocalDateTime(subscriber.getServiceStartDate().plusDays(2));
        MobileSubscriberDto newObj = MobileSubscriberDto.builder().serviceStartDate(newDate).build();

        when(subscriberRepository.findById(anyLong())).thenReturn(Optional.of(subscriber));

        //when/then
        assertThrows(ValidationFailedException.class, () -> service.patchSubscriber(newObj, subscriber.getId()));

        verify(subscriberRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldNotPatchSubscriberWhenNothingChangedTest() {
        //given
        Long ownerId = subscriber.getOwnerId(), userId = subscriber.getUserId();
        MobileSubscriberDto newObj = MobileSubscriberDto.builder().msisdn(subscriber.getMsisdn()).userId(userId).ownerId(ownerId)
                .serviceType(subscriber.getServiceType().name())
                .build();

        when(subscriberRepository.findById(anyLong())).thenReturn(Optional.of(subscriber));
        when(customerRepository.findById(userId)).thenReturn(Optional.of(subscriber.getUser()));
        when(customerRepository.findById(ownerId)).thenReturn(Optional.of(subscriber.getOwner()));

        //when
        MobileSubscriberDto updatedCustomer = service.patchSubscriber(newObj, subscriber.getId());

        //then
        assertThat(updatedCustomer, notNullValue());
        verify(customerRepository, times(1)).findById(userId);
        verify(customerRepository, times(1)).findById(ownerId);
        verify(subscriberRepository, times(1)).findById(anyLong());
        verify(subscriberRepository, never()).save(any());
    }

    @Test
    void shouldNotUpdateSubscriberWhenEmptyChangeProvidedTest() {
        //given
        MobileSubscriberDto dto = new MobileSubscriberDto();
        when(subscriberRepository.findById(anyLong())).thenReturn(Optional.of(subscriber));

        //when
        MobileSubscriberDto updatedCustomer = service.patchSubscriber(dto, subscriber.getId());

        //then
        assertThat(updatedCustomer, notNullValue());
        verify(customerRepository, never()).findById(anyLong());
        verify(subscriberRepository, times(1)).findById(anyLong());
        verify(subscriberRepository, never()).save(any());
    }

    @Test
    void shouldPatchSubscriberWhenAllowedDataChangedTest() {
        //given
        Customer newUser = Person.builder().id(99L).build();
        Customer newOwner = Person.builder().id(98L).build();
        String newServiceType = ServiceType.MOBILE_PREPAID.name();

        MobileSubscriberDto newObj = MobileSubscriberDto.builder().userId(newUser.getId())
                .ownerId(newOwner.getId()).serviceType(newServiceType)
                .build();

        when(subscriberRepository.findById(subscriber.getId())).thenReturn(Optional.of(subscriber));
        when(customerRepository.findById(newUser.getId())).thenReturn(Optional.of(newUser));
        when(customerRepository.findById(newOwner.getId())).thenReturn(Optional.of(newOwner));
        when(subscriberRepository.save(any())).thenReturn(MobileSubscriber.builder()
                .owner(newOwner)
                .user(newUser)
                .serviceType(ServiceType.valueOf(newServiceType))
                .build());

        //when
        MobileSubscriberDto updatedCustomer = service.patchSubscriber(newObj, subscriber.getId());

        //then
        assertThat(updatedCustomer, notNullValue());
        assertThat(updatedCustomer.getUserId(), is(newUser.getId()));
        assertThat(updatedCustomer.getOwnerId(), is(newOwner.getId()));
        assertThat(updatedCustomer.getServiceType(), is(newServiceType));

        verify(customerRepository, times(1)).findById(newUser.getId());
        verify(customerRepository, times(1)).findById(newOwner.getId());
        verify(subscriberRepository, times(1)).findById(subscriber.getId());
        verify(subscriberRepository, times(1)).save(any());
    }

    @Test
    void shouldDeleteSubscriberByIdTest() {
        //given
        when(subscriberRepository.existsById(anyLong())).thenReturn(true);

        //when
        service.deleteSubscriberById(1L);

        //then
        verify(subscriberRepository, times(1)).deleteById(1L);
    }
}
