package com.hubert.mobilerest.repositories;

import com.hubert.mobilerest.domain.MobileSubscriber;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository for MobileSubscriber database operations
 */
public interface MobileSubscriberRepository extends CrudRepository<MobileSubscriber, Long> {

    /**
     * Finding Mobile Subscriber by given optional criteria parameters
     * @param criteria Criteria object for finding the data
     * @return List of objects matching criteria
     */
    @Query("SELECT m FROM MobileSubscriber m " +
            "WHERE (:#{#criteria == null ? null : #criteria.msisdn} IS NULL OR :#{#criteria == null ? null : #criteria.msisdn} = msIsdn) " +
            "   AND (:#{#criteria == null ? null : #criteria.ownerId} IS NULL OR :#{#criteria == null ? null : #criteria.ownerId} = m.owner.id) " +
            "   AND (:#{#criteria == null ? null : #criteria.userId} IS NULL OR :#{#criteria == null ? null : #criteria.userId} = m.user.id) " +
            "   AND (:#{#criteria == null ? null : #criteria.serviceType} IS NULL OR :#{#criteria == null ? null : #criteria.serviceType} = m.serviceType) " +
            "   AND (:#{#criteria == null ? null : #criteria.serviceStartDate} IS NULL OR :#{#criteria == null ? null : #criteria.serviceStartDate} = m.serviceStartDate)")
    List<MobileSubscriber> findByCriteria(@Param("criteria") MobileSubscriber criteria);

    /**
     * Find first numeber wiht given msdnid
     * @param msdnid Msding to search for
     * @return Mobile subscriber with given number
     */
    Optional<MobileSubscriber> findFirstByMsisdn(String msdnid);

    /**
     * Verifies if there is a customer with provided msisdn and different id
     * @param msisdn Msisdn to search
     * @param id Id to skip
     * @return True if there is an entry with provided msisdn and different id
     */
    boolean existsByMsisdnAndIdNot(String msisdn, Long id);
}
