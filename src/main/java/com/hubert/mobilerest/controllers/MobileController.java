package com.hubert.mobilerest.controllers;

import com.hubert.mobilerest.dto.v1.MobileSubscriberDto;
import com.hubert.mobilerest.dto.v1.MobileSubscribersDto;
import com.hubert.mobilerest.services.MobileSubscriberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(description = "Controller providing operations on mobile number subscribers")
@Slf4j
@RestController
@RequestMapping(value = "/mobile/subscribers", produces = {"application/json", "application/json;v=1"})
public class MobileController {

    private MobileSubscriberService mobileSubscriberService;

    public MobileController(MobileSubscriberService mobileSubscriberService) {
        this.mobileSubscriberService = mobileSubscriberService;
    }

    @ApiOperation(value = "Get subscriber data by id provided on path")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Element found and processed successfully"),
            @ApiResponse(code = 404, message = "No subscribers found for provided id")
    })
    @GetMapping("/{id}")
    public MobileSubscriberDto findById(@PathVariable Long id) {
        return mobileSubscriberService.findSubscriberById(id);
    }

    @ApiOperation(value = "Allows to find all mobile numbers (if no criteria provided) or obtain the mobile numbers using criteria combinations")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Elements found and processed successfully"),
            @ApiResponse(code = 404, message = "No subscribers found for provided criteria")
    })
    @GetMapping
    public MobileSubscribersDto findByCriteria(@RequestParam(required = false) String msisdn,
                                               @RequestParam(required = false) Long ownerId,
                                               @RequestParam(required = false) Long userId,
                                               @RequestParam(required = false) String serviceType,
                                               @RequestParam(required = false) Long serviceStartDate) {
        return mobileSubscriberService.findSubscribersByCriteria(MobileSubscriberDto.builder()
                .msisdn(msisdn)
                .ownerId(ownerId)
                .userId(userId)
                .serviceType(serviceType)
                .serviceStartDate(serviceStartDate)
                .build());
    }

    @ApiOperation(value = "Add new mobile number to the database", notes = "Mobile number can be added only once")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Subscriber created successfully"),
            @ApiResponse(code = 400, message = "Wrong data provided"),
            @ApiResponse(code = 404, message = "Customer or owner not found")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public MobileSubscriberDto createNewSubscriber(@Valid @RequestBody MobileSubscriberDto mobileSubscriberDto) {
        return mobileSubscriberService.createNewSubscriber(mobileSubscriberDto);
    }

    @ApiOperation(value = "Update existing subscriber", notes = "User can only change mobile plan and assign different owners and users")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Subscriber updated successfully"),
            @ApiResponse(code = 400, message = "Wrong data provided"),
            @ApiResponse(code = 404, message = "Subscriber, customer or owner not found")
    })
    @PutMapping("/{id}")
    public MobileSubscriberDto updateSubscriber(@Valid @RequestBody MobileSubscriberDto mobileSubscriberDto, @PathVariable Long id) {
        return mobileSubscriberService.updateSubscriber(mobileSubscriberDto, id);
    }

    @ApiOperation(value = "Patch existing subscriber", notes = "User can only change mobile plan and assign different owners and users")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Subscriber patched successfully"),
            @ApiResponse(code = 400, message = "Wrong data provided"),
            @ApiResponse(code = 404, message = "Subscriber, customer or owner not found")
    })
    @PatchMapping("/{id}")
    public MobileSubscriberDto patchSubscriber(@RequestBody MobileSubscriberDto mobileSubscriberDto, @PathVariable Long id) {
        return mobileSubscriberService.patchSubscriber(mobileSubscriberDto, id);
    }

    @ApiOperation(value = "Remove existing subscriber")
    @DeleteMapping("/{id}")
    public void deleteSubscriber(@PathVariable Long id) {
        mobileSubscriberService.deleteSubscriberById(id);
    }
}
