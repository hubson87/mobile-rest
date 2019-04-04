package com.hubert.mobilerest.repositories;

import com.hubert.mobilerest.domain.Customer;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for customer related database operations
 */
public interface CustomerRepository extends CrudRepository<Customer, Long> {
}
