package com.hubert.mobilerest;

import com.hubert.mobilerest.domain.Company;
import com.hubert.mobilerest.domain.Customer;
import com.hubert.mobilerest.domain.MobileSubscriber;
import com.hubert.mobilerest.domain.Person;
import com.hubert.mobilerest.dto.v1.MobileSubscriberDto;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

class EqualsAndHashCodeTest {

    @Test
    void personTest() {
        EqualsVerifier.forClass(Person.class)
                .withOnlyTheseFields("firstName", "lastName", "documentId")
                .withPrefabValues(MobileSubscriber.class, MobileSubscriber.builder().msisdn("1").build(), MobileSubscriber.builder().build())
                .verify();
    }

    @Test
    void companyTest() {
        EqualsVerifier.forClass(Company.class)
                .withOnlyTheseFields("companyName", "taxId")
                .withPrefabValues(MobileSubscriber.class, MobileSubscriber.builder().msisdn("1").build(), MobileSubscriber.builder().build())
                .verify();
    }

    @Test
    void mobileSubscriberTest() {
        EqualsVerifier.forClass(MobileSubscriber.class)
                .withOnlyTheseFields("msisdn")
                .withPrefabValues(Customer.class, Person.builder().firstName("AA").build(), Company.builder().taxId("123").build())
                .verify();
    }

    @Test
    void mobileSubscriberDtoTest() {
        EqualsVerifier.forClass(MobileSubscriberDto.class)
                .suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS)
                .verify();
    }
}
