package com.hubert.mobilerest.repositories;

import com.hubert.mobilerest.domain.Company;
import com.hubert.mobilerest.domain.MobileSubscriber;
import com.hubert.mobilerest.domain.Person;
import com.hubert.mobilerest.domain.ServiceType;
import com.hubert.mobilerest.utils.DateUtils;
import org.apache.commons.collections4.IterableUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:propagateTestDb.sql")
class MobileSubscriberRepositoryIT {

    @Autowired
    MobileSubscriberRepository mobileSubscriberRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Test
    void shouldFetchAllSubscribersTest() {
        //when
        Iterable<MobileSubscriber> subscribers = mobileSubscriberRepository.findAll();

        //then
        assertThat(subscribers, notNullValue());
        assertThat(IterableUtils.size(subscribers), is(8));
    }

    @Test
    void shouldCrateNewSubscriberTest() {
        //given
        MobileSubscriber subscriber = MobileSubscriber.builder()
                .msisdn("123")
                .owner(customerRepository.findById(1L).orElse(null))
                .user(customerRepository.findById(2L).orElse(null))
                .serviceType(ServiceType.MOBILE_POSTPAID)
                .serviceStartDate(LocalDateTime.now())
                .build();

        //when
        MobileSubscriber savedSubscriber = mobileSubscriberRepository.save(subscriber);

        //then
        assertThat(savedSubscriber, notNullValue());
        assertThat(savedSubscriber.getId(), notNullValue());
    }

    @Test
    void shouldUpdateSubscriberTest() {
        //given
        Optional<MobileSubscriber> subscriber = mobileSubscriberRepository.findById(1L);
        assertTrue((subscriber).isPresent());
        subscriber.get().setMsisdn("+48000000001");

        //when
        MobileSubscriber updatedSubscriber = mobileSubscriberRepository.save(subscriber.get());

        //then
        assertThat(updatedSubscriber, notNullValue());
        assertThat(updatedSubscriber.getMsisdn(), is(subscriber.get().getMsisdn()));
    }

    @Test
    void shouldDeleteSubscriberTest() {
        //when
        mobileSubscriberRepository.deleteById(1L);

        //then
        Optional<MobileSubscriber> s = mobileSubscriberRepository.findById(1L);
        assertThat(s.isPresent(), is(false));
    }

    @Test
    void shouldDeleteNotPresentSubscriberTest() {
        //when/then
        assertThrows(EmptyResultDataAccessException.class, () -> mobileSubscriberRepository.deleteById(10000L));
    }

    @Test
    void shouldFindSubscriberByMsisdnTest() {
        //given
        MobileSubscriber criteria = MobileSubscriber.builder().msisdn("48532432456").build();

        //when
        List<MobileSubscriber> foundSubscribers =
                mobileSubscriberRepository.findByCriteria(criteria);

        //then
        assertThat(foundSubscribers, notNullValue());
        assertThat(foundSubscribers, hasSize(1));
        assertThat(foundSubscribers.get(0), hasProperty("id", is(3L)));
    }

    @Test
    void shouldFindSubscriberByOwnerIdTest() {
        //given
        MobileSubscriber criteria = MobileSubscriber.builder()
                .owner(Person.builder().id(1L).build())
                .build();

        //when
        List<MobileSubscriber> foundSubscribers =
                mobileSubscriberRepository.findByCriteria(criteria);

        //then
        assertThat(foundSubscribers, notNullValue());
        assertThat(foundSubscribers, hasSize(3));
        assertThat(foundSubscribers.stream().map(MobileSubscriber::getId).collect(Collectors.toList()),
                containsInAnyOrder(1L, 2L, 3L));
        List<Long> foundOwnerIds = foundSubscribers.stream().map(MobileSubscriber::getOwnerId).distinct().collect(Collectors.toList());
        assertThat(foundOwnerIds, hasSize(1));
        assertThat(foundOwnerIds.get(0), is(1L));
    }

    @Test
    void shouldNotFindNotExistingSubscriberTest() {
        //given
        MobileSubscriber criteria = MobileSubscriber.builder()
                .user(Person.builder().id(115L).build())
                .build();

        //when
        List<MobileSubscriber> foundSubscribers = mobileSubscriberRepository.findByCriteria(criteria);

        //then
        assertThat(foundSubscribers, notNullValue());
        assertThat(foundSubscribers, hasSize(0));
    }

    @Test
    void shouldFindSubscriberByUserIdTest() {
        //given
        MobileSubscriber criteria = MobileSubscriber.builder()
                .user(Person.builder().id(5L).build())
                .build();

        //when
        List<MobileSubscriber> foundSubscribers = mobileSubscriberRepository.findByCriteria(criteria);

        //then
        assertThat(foundSubscribers, notNullValue());
        assertThat(foundSubscribers, hasSize(3));
        assertThat(foundSubscribers.stream().map(MobileSubscriber::getId).collect(Collectors.toList()),
                containsInAnyOrder(4L, 5L, 6L));
        List<Long> foundUserIds = foundSubscribers.stream().map(MobileSubscriber::getUserId).distinct().collect(Collectors.toList());
        assertThat(foundUserIds, hasSize(1));
        assertThat(foundUserIds.get(0), is(5L));
    }

    @Test
    void shouldFindSubscriberByServiceTypeTest() {
        //given
        MobileSubscriber criteria = MobileSubscriber.builder()
                .serviceType(ServiceType.MOBILE_POSTPAID)
                .build();

        //when
        List<MobileSubscriber> foundSubscribers = mobileSubscriberRepository.findByCriteria(criteria);

        //then
        assertThat(foundSubscribers, notNullValue());
        assertThat(foundSubscribers, hasSize(4));
        assertThat(foundSubscribers.stream().map(MobileSubscriber::getId).collect(Collectors.toList()),
                containsInAnyOrder(3L, 4L, 5L, 6L));
    }

    @Test
    void shouldFindSubscriberByServiceServiceStartDateTest() {
        //given
        MobileSubscriber criteria = MobileSubscriber.builder()
                .serviceStartDate(DateUtils.localDateTimeFromEpoch(1554308106460L))
                .build();

        //when
        List<MobileSubscriber> foundSubscribers = mobileSubscriberRepository.findByCriteria(criteria);

        //then
        assertThat(foundSubscribers, notNullValue());
        assertThat(foundSubscribers, hasSize(4));
        assertThat(foundSubscribers.stream().map(MobileSubscriber::getId).collect(Collectors.toList()),
                containsInAnyOrder(1L, 3L, 4L, 5L));
    }

    @Test
    void shouldFindSubscribersByAllParametersTest() {
        //given
        MobileSubscriber criteria = MobileSubscriber.builder()
                .serviceType(ServiceType.MOBILE_POSTPAID)
                .owner(Person.builder().id(2L).build())
                .user(Company.builder().id(5L).build())
                .msisdn("48549381237")
                .serviceStartDate(DateUtils.localDateTimeFromEpoch(1554308106460L))
                .build();

        //when
        List<MobileSubscriber> foundSubscribers = mobileSubscriberRepository.findByCriteria(criteria);

        //then
        assertThat(foundSubscribers, notNullValue());
        assertThat(foundSubscribers, hasSize(1));
        MobileSubscriber foundSubscriber = foundSubscribers.get(0);
        assertThat(foundSubscriber.getId(), is(4L));
        assertThat(foundSubscriber.getServiceType(), is(criteria.getServiceType()));
        assertThat(foundSubscriber.getOwner(), notNullValue());
        assertThat(foundSubscriber.getOwner().getId(), is(criteria.getOwnerId()));
        assertThat(foundSubscriber.getUser(), notNullValue());
        assertThat(foundSubscriber.getUser().getId(), is(criteria.getUserId()));
        assertThat(foundSubscriber.getMsisdn(), is(criteria.getMsisdn()));
        assertThat(foundSubscriber.getServiceStartDate(), is(criteria.getServiceStartDate()));
    }

    @Test
    void shouldNotFindSubscribersByNoParametersTest() {
        //given
        MobileSubscriber criteria = MobileSubscriber.builder().build();

        //when
        List<MobileSubscriber> foundSubscribers = mobileSubscriberRepository.findByCriteria(criteria);

        //then
        assertThat(foundSubscribers, notNullValue());
        assertThat(foundSubscribers, hasSize(8));
    }

    @Test
    void shouldNotFindSubscribersByNullTest() {
        //when
        List<MobileSubscriber> foundSubscribers = mobileSubscriberRepository.findByCriteria(null);

        //then
        assertThat(foundSubscribers, notNullValue());
        assertThat(foundSubscribers, hasSize(8));
    }

    @Test
    void shouldFindByMsdnidTest() {
        //given
        String msdnId = "48549381237";

        //when
        Optional<MobileSubscriber> mobileSubscriber = mobileSubscriberRepository.findFirstByMsisdn(msdnId);

        //then
        assertThat(mobileSubscriber.isPresent(), is(true));
        assertThat(mobileSubscriber.get().getId(), is(4L));
    }

    @Test
    void shouldNotFindByMsdnidTest() {
        //given
        String msdnId = "000000";

        //when
        Optional<MobileSubscriber> mobileSubscriber = mobileSubscriberRepository.findFirstByMsisdn(msdnId);

        //then
        assertThat(mobileSubscriber.isPresent(), is(false));
    }

    @Test
    void shouldFindByMsisdnAndNotIdTest() {
        //given
        String msisdn = "48549381237";
        Long id = 5L;

        //when
        boolean found = mobileSubscriberRepository.existsByMsisdnAndIdNot(msisdn, id);

        //then
        assertThat(found, is(true));
    }

    @Test
    void shouldNotFindByMsisdnAndNotIdTest() {
        //given
        String msisdn = "48549381237";
        Long id = 4L;

        //when
        boolean found = mobileSubscriberRepository.existsByMsisdnAndIdNot(msisdn, id);

        //then
        assertThat(found, is(false));
    }
}
