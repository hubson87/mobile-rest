package com.hubert.mobilerest.services;

import com.hubert.mobilerest.domain.Customer;
import com.hubert.mobilerest.domain.MobileSubscriber;
import com.hubert.mobilerest.dto.v1.MobileSubscriberDto;
import com.hubert.mobilerest.dto.v1.MobileSubscribersDto;
import com.hubert.mobilerest.exceptions.ResourceNotFoundException;
import com.hubert.mobilerest.exceptions.ValidationFailedException;
import com.hubert.mobilerest.mappers.MobileSubscriberMapper;
import com.hubert.mobilerest.repositories.CustomerRepository;
import com.hubert.mobilerest.repositories.MobileSubscriberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service for manipulating subscribers data
 */
@Slf4j
@Service
public class MobileSubscriberServiceImpl implements MobileSubscriberService {

    private MobileSubscriberRepository subscriberRepository;
    private CustomerRepository customerRepository;
    private MobileSubscriberMapper mobileSubscriberMapper;

    public MobileSubscriberServiceImpl(MobileSubscriberRepository subscriberRepository, CustomerRepository customerRepository, MobileSubscriberMapper mobileSubscriberMapper) {
        this.subscriberRepository = subscriberRepository;
        this.customerRepository = customerRepository;
        this.mobileSubscriberMapper = mobileSubscriberMapper;
    }

    /**
     * Returning subscriber for provided id. ResourceNotFound will be thrown if no customer with given id present
     * @param id Subscriber id
     * @return Found subscriber with id
     */
    @Override
    public MobileSubscriberDto findSubscriberById(Long id) {
        Optional<MobileSubscriber> subscriber = subscriberRepository.findById(id);
        if (subscriber.isPresent()) {
            return mobileSubscriberMapper.domainToDto(subscriber.get());
        } else {
            log.error("Cannot find subscriber with id: " + id);
            throw new ResourceNotFoundException("Subscriber not found");
        }
    }

    /**
     * Search subscribers by criteria.
     * If no criteria provided, then it lists all the subscribers in the database
     * @param dtoCriteria Criteria parameters mapped as dto object
     * @return Subscribers matching provided criteria. If all values are empty, then it lists all the subscribers in the database
     */
    @Override
    public MobileSubscribersDto findSubscribersByCriteria(MobileSubscriberDto dtoCriteria) {
        Iterable<MobileSubscriber> result;
        if (dtoCriteria == null || dtoCriteria.isEmpty()) {     //for optimization
            result = subscriberRepository.findAll();
        } else {
            Customer owner = dtoCriteria.getOwnerId() != null ? new Customer(dtoCriteria.getOwnerId()) : null;
            Customer user = dtoCriteria.getUserId() != null ? new Customer(dtoCriteria.getUserId()) : null;
            MobileSubscriber mobileSubscriberCriteria = mobileSubscriberMapper.dtoToDomain(dtoCriteria, owner, user);
            result = subscriberRepository.findByCriteria(mobileSubscriberCriteria);
        }
        return new MobileSubscribersDto(StreamSupport.stream(result.spliterator(), true)
                .map(mobileSubscriberMapper::domainToDto)
                .collect(Collectors.toList()));
    }

    /**
     * Creating new subscriber for provided data if not msisdn not present yet
     * @param dtoToPersist Customer data that needs to be persisted
     * @return Added subscriber data
     */
    @Override
    @Transactional
    public MobileSubscriberDto createNewSubscriber(@Valid @NotNull MobileSubscriberDto dtoToPersist) {
        if (subscriberRepository.findFirstByMsisdn(dtoToPersist.getMsisdn()).isPresent()) {
            log.error("Subscriber with number " + dtoToPersist.getMsisdn() + " already exists in the database");
            throw new ValidationFailedException("Msdnid already exists");
        }
        if (dtoToPersist.getServiceStartDate() != null) {
            log.error("Customer provided service start date for creation of " + dtoToPersist.getMsisdn());
            throw new ValidationFailedException("Service start date will be calculated automatically, so it shouldn't been provided");
        }
        Customer owner = obtainCustomer(dtoToPersist.getOwnerId());
        Customer user = obtainCustomer(dtoToPersist.getUserId());
        MobileSubscriber subscriber = mobileSubscriberMapper.dtoToDomain(dtoToPersist, owner, user);

        subscriber.setServiceStartDate(LocalDateTime.now());
        MobileSubscriber savedSubscriber = subscriberRepository.save(subscriber);
        log.info("Subscriber created with id: " + savedSubscriber.getId());
        return mobileSubscriberMapper.domainToDto(savedSubscriber);
    }

    /**
     * Updating subsciber method. Allows only on updates of PlanType, Owner, User. All the other changes are rejected with ValidationException
     * @param dtoToUpdate Provided subscriber with applied changes
     * @param id Id of existing subscriber that needs to be updated
     * @return Updated subscriber data
     */
    @Override
    @Transactional
    public MobileSubscriberDto updateSubscriber(@Valid @NotNull MobileSubscriberDto dtoToUpdate, @NotNull Long id) {
        Optional<MobileSubscriber> dbSubscriber = subscriberRepository.findById(id);
        if (dbSubscriber.isEmpty()) {
            log.error("Subscriber for update " + id + " not found");
            throw new ResourceNotFoundException("Subscriber not found");
        }
        Customer owner = obtainCustomer(dtoToUpdate.getOwnerId());
        Customer user = obtainCustomer(dtoToUpdate.getUserId());
        MobileSubscriber subscriber = mobileSubscriberMapper.dtoToDomain(dtoToUpdate, owner, user);
        boolean hasChanged = validateChangesAndPreparePatchObjectIfNeeded(dbSubscriber.get(), subscriber, false);
        if (hasChanged) {
            subscriber.setId(id);
            subscriber.setServiceStartDate(dbSubscriber.get().getServiceStartDate());
            MobileSubscriber updatedSubscriber = subscriberRepository.save(subscriber);
            log.info("Subscriber with id: " + id + " successfully updated");
            return mobileSubscriberMapper.domainToDto(updatedSubscriber);
        } else {
            log.info("No changes to update for subscriber with id: " + id);
            return mobileSubscriberMapper.domainToDto(dbSubscriber.get());
        }
    }

    /**
     * Patching subsciber method. Allows only patching of PlanType, Owner, User. All the other changes are rejected with ValidationException
     * @param dtoToPatch Provided subscriber changes
     * @param id Id of existing subscriber that needs to be patched
     * @return Updated subscriber data
     */
    @Override
    @Transactional
    public MobileSubscriberDto patchSubscriber(@NotNull MobileSubscriberDto dtoToPatch, @NotNull Long id) {
        Optional<MobileSubscriber> dbSubscriberOpt = subscriberRepository.findById(id);
        if (dbSubscriberOpt.isEmpty()) {
            log.error("Subscriber for patch " + id + " not found");
            throw new ResourceNotFoundException("Subscriber not found");
        }
        Customer owner = dtoToPatch.getOwnerId() != null ? obtainCustomer(dtoToPatch.getOwnerId()) : null;
        Customer user = dtoToPatch.getUserId() != null ? obtainCustomer(dtoToPatch.getUserId()) : null;
        MobileSubscriber subscriber = mobileSubscriberMapper.dtoToDomain(dtoToPatch, owner, user);
        MobileSubscriber dbSubscriber = dbSubscriberOpt.get();
        boolean objChanged = validateChangesAndPreparePatchObjectIfNeeded(dbSubscriber, subscriber, true);
        if (objChanged) {
            MobileSubscriber updatedSubscriber = subscriberRepository.save(dbSubscriber);
            log.info("Subscriber with id: " + id + " successfully patched");
            return mobileSubscriberMapper.domainToDto(updatedSubscriber);
        } else {
            log.info("Subscriber with id: " + id + " not patched. Nothing to update");
            return mobileSubscriberMapper.domainToDto(dbSubscriber);
        }
    }

    /**
     * Validation what can be updated and what's user trying to update. Also allows to prepare the patch object
     * @param dbSubscriber Subscriber from database
     * @param subscriber Subscriber provided by user (update/patch)
     * @param patch If flag is set to true, then function will patch user object on db object
     * @return True if there was a patch provided and changed the original object
     */
    private boolean validateChangesAndPreparePatchObjectIfNeeded(MobileSubscriber dbSubscriber, MobileSubscriber subscriber, boolean patch) {
        boolean hasChanged = false;
        if (subscriber.getMsisdn() != null && !subscriber.getMsisdn().equals(dbSubscriber.getMsisdn())) {
            log.error("Cannot update msisdn id. Attempt made for id " + dbSubscriber.getId());
            throw new ValidationFailedException("Msisdn updates are not allowed");
        }
        if (subscriber.getUserId() != null && !subscriber.getUserId().equals(dbSubscriber.getUserId())) {
            if (patch) {
                dbSubscriber.assignUser(subscriber.getUser());
            }
            hasChanged = true;
        }
        if (subscriber.getOwnerId() != null && !subscriber.getOwnerId().equals(dbSubscriber.getOwnerId())) {
            if (patch) {
                dbSubscriber.assignOwner(subscriber.getOwner());
            }
            hasChanged = true;
        }
        if (subscriber.getServiceType() != null && !subscriber.getServiceType().equals(dbSubscriber.getServiceType())) {
            if (patch) {
                dbSubscriber.setServiceStartDate(subscriber.getServiceStartDate());
            }
            hasChanged = true;
        }
        if (subscriber.getServiceStartDate() != null) {
            log.error("Cannot update service start date. Attempt made for id " + dbSubscriber.getId());
            throw new ValidationFailedException("Service start date updates are not allowed");
        }
        return hasChanged;
    }

    /**
     * Delete of subscriber
     * @param id Id of subscriber that needs to be removed
     */
    @Override
    @Transactional
    public void deleteSubscriberById(Long id) {
        if (subscriberRepository.existsById(id)) {
            subscriberRepository.deleteById(id);
        }
        log.info("Subscriber with id: " + id + "successfully removed");
    }

    /**
     * Receiving customer from database. If not present, then NotFound exception will be thrown
     * @param customerId Customer id
     * @return Obtained customer
     */
    private Customer obtainCustomer(Long customerId) {
        Optional<Customer> customer = customerRepository.findById(customerId);
        if (customer.isPresent()) {
            return customer.get();
        }
        log.error("Customer with id: " + customerId + " cannot be found in the database!");
        throw new ResourceNotFoundException("Customer " + customerId + " not found");
    }
}
