package com.hubert.mobilerest.controllers.v1;

import com.hubert.mobilerest.domain.MobileSubscriber;
import com.hubert.mobilerest.dto.v1.MobileSubscriberDto;
import com.hubert.mobilerest.dto.v1.MobileSubscribersDto;
import com.hubert.mobilerest.mappers.v1.MobileSubscriberMapper;
import com.hubert.mobilerest.services.MobileSubscriberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
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
import java.util.List;
import java.util.stream.Collectors;

@Api(description = "Controller providing operations on mobile number subscribers")
@Slf4j
@RestController
@RequestMapping(value = "/mobile/subscribers", produces = {"application/json", "application/json;v=1"})
public class MobileController {

    private MobileSubscriberMapper mobileSubscriberMapper;
    private MobileSubscriberService mobileSubscriberService;

    public MobileController(MobileSubscriberMapper mobileSubscriberMapper, MobileSubscriberService mobileSubscriberService) {
        this.mobileSubscriberMapper = mobileSubscriberMapper;
        this.mobileSubscriberService = mobileSubscriberService;
    }

    @ApiOperation(value = "Get subscriber data by id provided on path")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Element found and processed successfully"),
            @ApiResponse(code = 404, message = "No subscribers found for provided id")
    })
    @GetMapping("/{id}")
    public Resource<MobileSubscriberDto> findById(@PathVariable Long id) {
        MobileSubscriberDto res = mobileSubscriberMapper.domainToDto(mobileSubscriberService.findSubscriberById(id));
        ControllerLinkBuilder linkToFindById = ControllerLinkBuilder.linkTo(ControllerLinkBuilder
                .methodOn(this.getClass()).findByCriteria(null, null, null, null, null));
        return new Resource<>(res, linkToFindById.withRel("find-all").expand(null, null, null, null, null));
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
        MobileSubscriberDto dtoCriteria = MobileSubscriberDto.builder()
                .msisdn(msisdn)
                .ownerId(ownerId)
                .userId(userId)
                .serviceType(serviceType)
                .serviceStartDate(serviceStartDate)
                .build();
        MobileSubscriber searchCriteria = mobileSubscriberMapper.dtoToDomain(dtoCriteria);
        List<MobileSubscriber> subscribers = mobileSubscriberService.findSubscribersByCriteria(searchCriteria);

        return new MobileSubscribersDto(subscribers.stream()
                .map(mobileSubscriberMapper::domainToDto)
                .collect(Collectors.toList()));
    }

    @ApiOperation(value = "Add new mobile number to the database", notes = "Mobile number can be added only once")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Subscriber created successfully"),
            @ApiResponse(code = 400, message = "Wrong data provided"),
            @ApiResponse(code = 404, message = "Customer or owner not found")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Resource<MobileSubscriberDto> createNewSubscriber(@Valid @RequestBody MobileSubscriberDto mobileSubscriberDto) {
        MobileSubscriber savedSubscriber = mobileSubscriberService.createNewSubscriber(mobileSubscriberMapper.dtoToDomain(mobileSubscriberDto));
        MobileSubscriberDto res = mobileSubscriberMapper.domainToDto(savedSubscriber);
        ControllerLinkBuilder linkToFindById = ControllerLinkBuilder.linkTo(ControllerLinkBuilder
                .methodOn(this.getClass()).findById(savedSubscriber.getId()));
        return new Resource<>(res, linkToFindById.withRel("find-by-id"));
    }

    @ApiOperation(value = "Update existing subscriber", notes = "User can only change mobile plan and assign different owners and users")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Subscriber updated successfully"),
            @ApiResponse(code = 400, message = "Wrong data provided"),
            @ApiResponse(code = 404, message = "Subscriber, customer or owner not found")
    })
    @PutMapping("/{id}")
    public Resource<MobileSubscriberDto> updateSubscriber(@Valid @RequestBody MobileSubscriberDto mobileSubscriberDto, @PathVariable Long id) {
        MobileSubscriber updateSubscriber = mobileSubscriberService.updateSubscriber(mobileSubscriberMapper.dtoToDomain(mobileSubscriberDto), id);
        ControllerLinkBuilder linkToFindById = ControllerLinkBuilder.linkTo(ControllerLinkBuilder
                .methodOn(this.getClass()).findById(id));
        MobileSubscriberDto res = mobileSubscriberMapper.domainToDto(updateSubscriber);
        return new Resource<>(res, linkToFindById.withRel("find-by-id"));
    }

    @ApiOperation(value = "Patch existing subscriber", notes = "User can only change mobile plan and assign different owners and users")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Subscriber patched successfully"),
            @ApiResponse(code = 400, message = "Wrong data provided"),
            @ApiResponse(code = 404, message = "Subscriber, customer or owner not found")
    })
    @PatchMapping("/{id}")
    public Resource<MobileSubscriberDto> patchSubscriber(@RequestBody MobileSubscriberDto mobileSubscriberDto, @PathVariable Long id) {
        MobileSubscriber patchedSubscriber = mobileSubscriberService.patchSubscriber(mobileSubscriberMapper.dtoToDomain(mobileSubscriberDto), id);
        MobileSubscriberDto res = mobileSubscriberMapper.domainToDto(patchedSubscriber);
        ControllerLinkBuilder linkToFindById = ControllerLinkBuilder.linkTo(ControllerLinkBuilder
                .methodOn(this.getClass()).findById(id));
        return new Resource<>(res, linkToFindById.withRel("find-by-id"));
    }

    @ApiOperation(value = "Remove existing subscriber")
    @DeleteMapping("/{id}")
    public void deleteSubscriber(@PathVariable Long id) {
        mobileSubscriberService.deleteSubscriberById(id);
    }
}
