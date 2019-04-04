package com.hubert.mobilerest.services;

import com.hubert.mobilerest.dto.v1.MobileSubscriberDto;
import com.hubert.mobilerest.dto.v1.MobileSubscribersDto;

public interface MobileSubscriberService {
    MobileSubscriberDto findSubscriberById(Long id);
    MobileSubscribersDto findSubscribersByCriteria(MobileSubscriberDto dtoCriteria);
    MobileSubscriberDto createNewSubscriber(MobileSubscriberDto dtoToPersist);
    MobileSubscriberDto updateSubscriber(MobileSubscriberDto dtoToUpdate, Long id);
    MobileSubscriberDto patchSubscriber(MobileSubscriberDto dtoToPatch, Long id);
    void deleteSubscriberById(Long id);
}
