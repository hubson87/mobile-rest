package com.hubert.mobilerest.repositories;

import com.hubert.mobilerest.domain.Company;
import com.hubert.mobilerest.domain.Customer;
import com.hubert.mobilerest.domain.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:propagateTestDb.sql")
class CustomerRepositoryIT {

    @Autowired
    CustomerRepository customerRepository;

    @Test
    void shouldFindPersonByIdTest() {
        //given
        Long id = 1L;

        //when
        Optional<Customer> customer = customerRepository.findById(id);

        //then
        assertThat(customer.isPresent(), is(true));
        assertThat(customer.get(), instanceOf(Person.class));
    }

    @Test
    void shouldFindCompanyByIdTest() {
        //given
        Long id = 2L;

        //when
        Optional<Customer> customer = customerRepository.findById(id);

        //then
        assertThat(customer.isPresent(), is(true));
        assertThat(customer.get(), instanceOf(Company.class));
    }
}
