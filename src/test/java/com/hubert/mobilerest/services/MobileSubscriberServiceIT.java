package com.hubert.mobilerest.services;

import com.hubert.mobilerest.domain.Company;
import com.hubert.mobilerest.domain.Customer;
import com.hubert.mobilerest.domain.MobileSubscriber;
import com.hubert.mobilerest.domain.Person;
import com.hubert.mobilerest.domain.ServiceType;
import com.hubert.mobilerest.exceptions.ResourceNotFoundException;
import com.hubert.mobilerest.repositories.CustomerRepository;
import com.hubert.mobilerest.utils.DateUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:propagateTestDb.sql")
class MobileSubscriberServiceIT {

    @Autowired
    MobileSubscriberService mobileSubscriberService;

    @Autowired
    CustomerRepository customerRepository;

    @Test
    void shouldFindAllTest() {
        assertThat(mobileSubscriberService.findSubscribersByCriteria(null), hasSize(8));
    }

    @Test
    void shouldFindByAllCriteriaProvidedTest() {

    }

    @Test
    void shouldFindByIdTest() {
        //when
        MobileSubscriber subscriber = mobileSubscriberService.findSubscriberById(1L);

        //then
        assertThat(subscriber, notNullValue());
        assertThat(subscriber.getId(), is(1L));
        assertThat(subscriber.getMsisdn(), is("48500123456"));
        assertThat(subscriber.getUser(), notNullValue());
        assertThat(subscriber.getUser().getId(), is(2L));
        assertThat(subscriber.getUser(), instanceOf(Company.class));
        assertThat(((Company) subscriber.getUser()).getTaxId(), is("1231"));
        assertThat(subscriber.getOwner(), notNullValue());
        assertThat(subscriber.getOwner().getId(), is(1L));
        assertThat(subscriber.getOwner(), instanceOf(Person.class));
        assertThat(((Person) subscriber.getOwner()).getDocumentId(), is("1234"));
        assertThat(subscriber.getServiceType(), is(ServiceType.MOBILE_PREPAID));
        assertThat(subscriber.getServiceStartDate(), is(DateUtils.localDateTimeFromEpoch(1554308106460L)));
    }

    @Test
    void shouldCreateNewTest() {
        //given
        MobileSubscriber newSubscriber = MobileSubscriber.builder()
                .user(customerRepository.findById(3L).orElse(null))
                .owner(customerRepository.findById(1L).orElse(null))
                .msisdn("48222999303")
                .serviceType(ServiceType.MOBILE_PREPAID)
                .build();

        //when
        MobileSubscriber savedSubscriber = mobileSubscriberService.createNewSubscriber(newSubscriber);

        //then
        assertThat(savedSubscriber, notNullValue());
        assertThat(savedSubscriber.getId(), notNullValue());
        assertThat(savedSubscriber.getMsisdn(), is("48222999303"));
        assertThat(savedSubscriber.getServiceType(), is(ServiceType.MOBILE_PREPAID));
        assertThat(savedSubscriber.getOwnerId(), is(1L));
        assertThat(savedSubscriber.getUserId(), is(3L));
        assertThat(savedSubscriber.getServiceStartDate(), notNullValue());
    }

    @Test
    void shouldUpdateExistingTest() {
        //given
        MobileSubscriber existingSubscriber = mobileSubscriberService.findSubscriberById(1L);
        assertThat(existingSubscriber.getServiceType(), is(ServiceType.MOBILE_PREPAID));
        assertThat(existingSubscriber.getOwnerId(), is(1L));
        existingSubscriber.setId(null);

        //when
        existingSubscriber.setServiceType(ServiceType.MOBILE_POSTPAID);
        existingSubscriber.setOwner(customerRepository.findById(2L).orElse(null));
        MobileSubscriber updated = mobileSubscriberService.updateSubscriber(existingSubscriber, 1L);

        //then
        assertThat(updated, notNullValue());
        assertThat(updated.getId(), is(1L));
        assertThat(updated.getMsisdn(), is(existingSubscriber.getMsisdn()));
        assertThat(updated.getServiceType(), is(ServiceType.MOBILE_POSTPAID));
        assertThat(updated.getOwnerId(), is(2L));
        assertThat(updated.getUserId(), is(existingSubscriber.getUserId()));
        assertThat(updated.getServiceStartDate(), is(existingSubscriber.getServiceStartDate()));
    }

    @Test
    void shouldPatchExistingTest() {
        //given
        Long id = 1L;
        MobileSubscriber toPatch = MobileSubscriber.builder()
                .serviceType(ServiceType.MOBILE_POSTPAID)
                .user(new Customer(3L))
                .build();

        //when
        MobileSubscriber patched = mobileSubscriberService.patchSubscriber(toPatch, id);

        //then
        assertThat(patched, notNullValue());
        assertThat(patched.getId(), is(id));
        assertThat(patched.getMsisdn(), is("48500123456"));
        assertThat(patched.getServiceType(), is(ServiceType.MOBILE_POSTPAID));
        assertThat(patched.getOwnerId(), is(1L));
        assertThat(patched.getUserId(), is(3L));
        assertThat(patched.getServiceStartDate(), is(DateUtils.localDateTimeFromEpoch(1554308106460L)));
    }

    @Test
    void shouldDeleteExistingTest() {
        //when
        mobileSubscriberService.deleteSubscriberById(1L);

        //then
        assertThrows(ResourceNotFoundException.class, () -> mobileSubscriberService.findSubscriberById(1L));
    }
}
