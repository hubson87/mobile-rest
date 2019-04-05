package com.hubert.mobilerest.services;

import com.hubert.mobilerest.domain.MobileSubscriber;

import java.util.List;

public interface MobileSubscriberService {
    MobileSubscriber findSubscriberById(Long id);
    List<MobileSubscriber> findSubscribersByCriteria(MobileSubscriber criteria);
    MobileSubscriber createNewSubscriber(MobileSubscriber toPersist);
    MobileSubscriber updateSubscriber(MobileSubscriber toUpdate, Long id);
    MobileSubscriber patchSubscriber(MobileSubscriber toPatch, Long id);
    void deleteSubscriberById(Long id);
}
