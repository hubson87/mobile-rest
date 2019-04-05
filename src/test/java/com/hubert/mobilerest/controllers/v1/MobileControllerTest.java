package com.hubert.mobilerest.controllers.v1;

import com.hubert.mobilerest.controllers.v1.MobileController;
import com.hubert.mobilerest.domain.Company;
import com.hubert.mobilerest.domain.MobileSubscriber;
import com.hubert.mobilerest.domain.Person;
import com.hubert.mobilerest.domain.ServiceType;
import com.hubert.mobilerest.dto.v1.MobileSubscriberDto;
import com.hubert.mobilerest.exceptions.ResourceNotFoundException;
import com.hubert.mobilerest.exceptions.ValidationFailedException;
import com.hubert.mobilerest.mappers.v1.MobileSubscriberMapper;
import com.hubert.mobilerest.services.MobileSubscriberService;
import com.hubert.mobilerest.utils.DateUtils;
import com.hubert.mobilerest.utils.TestUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {MobileController.class})
@ExtendWith(SpringExtension.class)
class MobileControllerTest {
    private static final String CONTROLLER_BASE_URL = "/mobile/subscribers";

    @MockBean
    MobileSubscriberService service;

    @MockBean
    MobileSubscriberMapper mobileSubscriberMapper;

    @Autowired
    MockMvc mockMvc;

    private MobileSubscriber subscriber;
    private MobileSubscriber subscriber2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        subscriber = MobileSubscriber.builder().user(Person.builder().id(1L).build()).owner(Person.builder().id(2L).build()).msisdn("48123312123")
                .serviceType(ServiceType.MOBILE_POSTPAID).serviceStartDate(LocalDateTime.of(2019, 1, 1, 1, 1)).build();

        subscriber2 =  MobileSubscriber.builder().user(Person.builder().id(4L).build()).owner(Person.builder().id(2L).build()).msisdn("48123312123")
                .serviceType(ServiceType.MOBILE_POSTPAID).serviceStartDate(LocalDateTime.of(2019, 1, 1, 1, 1)).build();

        //let's use normal mapper for mapping the service results to be sure that has been used in service correctly
        MobileSubscriberMapper realMapper = Mappers.getMapper(MobileSubscriberMapper.class);
        when(mobileSubscriberMapper.domainToDto(any())).thenAnswer(param -> realMapper.domainToDto((MobileSubscriber)param.getArguments()[0]));
        when(mobileSubscriberMapper.dtoToDomain(any())).thenAnswer(param -> realMapper.dtoToDomain((MobileSubscriberDto)param.getArguments()[0]));
    }

    @Test
    void shouldFindByIdTest() throws Exception {
        //given
        given(service.findSubscriberById(1L)).willReturn(subscriber);

        //when/then
        mockMvc.perform(get(CONTROLLER_BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msisdn", is(subscriber.getMsisdn())));
    }

    @Test
    void shouldFindAllTest() throws Exception {
        //given
        given(service.findSubscribersByCriteria(MobileSubscriber.builder().build()))
                .willReturn(Lists.newArrayList(subscriber, subscriber2));

        //when/then
        mockMvc.perform(get(CONTROLLER_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subscribers", hasSize(2)));
    }

    @Test
    void shouldFindByCriteriaTest() throws Exception {
        given(service.findSubscribersByCriteria(
                MobileSubscriber.builder()
                        .msisdn("123123123")
                        .serviceType(ServiceType.MOBILE_POSTPAID)
                        .serviceStartDate(LocalDateTime.now())
                        .owner(Company.builder().id(1L).build())
                        .user(Company.builder().id(2L).build())
                        .build()))
                .willReturn(Lists.newArrayList(subscriber, subscriber2));

        //when/then
        mockMvc.perform(get(CONTROLLER_BASE_URL)
                .param("msisdn", "123123123")
                .param("userId", "1")
                .param("ownerId", "2")
                .param("serviceType", "MOBILE_POSTPAID")
                .param("serviceStartDate", "13123213")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subscribers", hasSize(2)));
    }

    @Test
    void shouldCreateNewSubscriberTest() throws Exception {
        //given
        MobileSubscriber subscriber = MobileSubscriber.builder()
                .serviceType(ServiceType.MOBILE_POSTPAID)
                .msisdn("49123123123")
                .owner(Company.builder().id(1L).build())
                .user(Company.builder().id(2L).build())
                .build();

        MobileSubscriber returnedSubscriber = MobileSubscriber.builder()
                .serviceType(ServiceType.MOBILE_PREPAID)
                .msisdn("49123123123")
                .owner(Company.builder().id(1L).build())
                .user(Company.builder().id(2L).build())
                .serviceStartDate(LocalDateTime.now())
                .build();

        given(service.createNewSubscriber(subscriber)).willReturn(returnedSubscriber);

        //when/then
        mockMvc.perform(post(CONTROLLER_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(subscriber)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.msisdn", is("49123123123")))
                .andExpect(jsonPath("$.serviceStartDate", is(DateUtils.epochFromLocalDateTime(returnedSubscriber.getServiceStartDate()))));
    }

    @Test
    void shouldUpdateSubscriberTest() throws Exception {
        //given
        MobileSubscriber subscriber = MobileSubscriber.builder()
                .serviceType(ServiceType.MOBILE_PREPAID)
                .msisdn("49123123123")
                .owner(Company.builder().id(1L).build())
                .user(Company.builder().id(2L).build())
                .build();

        MobileSubscriber retSub = MobileSubscriber.builder()
                .serviceType(ServiceType.MOBILE_PREPAID)
                .msisdn("49123123123")
                .owner(Company.builder().id(1L).build())
                .user(Company.builder().id(2L).build())
                .serviceStartDate(LocalDateTime.now())
                .build();

        given(service.updateSubscriber(subscriber, 1L)).willReturn(retSub);

        //when/then
        mockMvc.perform(put(CONTROLLER_BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(subscriber)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msisdn", is("49123123123")))
                .andExpect(jsonPath("$.serviceStartDate", is(DateUtils.epochFromLocalDateTime(retSub.getServiceStartDate()))));
    }

    @Test
    void shouldPatchSubscriberTest() throws Exception {
        //given
        MobileSubscriber subscriber = MobileSubscriber.builder()
                .owner(Company.builder().id(1L).build())
                .user(Company.builder().id(2L).build())
                .build();

        MobileSubscriber returned = MobileSubscriber.builder()
                .serviceType(ServiceType.MOBILE_PREPAID)
                .msisdn("49123123123")
                .owner(Company.builder().id(1L).build())
                .user(Company.builder().id(2L).build())
                .serviceStartDate(LocalDateTime.now())
                .build();

        given(service.patchSubscriber(subscriber, 1L)).willReturn(returned);

        //when/then
        mockMvc.perform(patch(CONTROLLER_BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(subscriber)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msisdn", is("49123123123")))
                .andExpect(jsonPath("$.serviceStartDate", is(DateUtils.epochFromLocalDateTime(returned.getServiceStartDate()))));
    }

    @Test
    void shouldDeleteSubscriberTest() throws Exception {
        //when/then
        mockMvc.perform(delete(CONTROLLER_BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).deleteSubscriberById(anyLong());
    }

    @Test
    void shouldThrowNotFoundExceptionTest() throws Exception {
        //given
        given(service.findSubscriberById(1L)).willThrow(ResourceNotFoundException.class);

        //when/then
        mockMvc.perform(get(CONTROLLER_BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldThrowInvalidRequestExceptionTest() throws Exception {
        //given
        MobileSubscriberDto dto = MobileSubscriberDto.builder()
                .ownerId(1L)
                .userId(2L)
                .build();

        given(service.patchSubscriber(any(), anyLong())).willThrow(ValidationFailedException.class);

        //when/then
        mockMvc.perform(patch(CONTROLLER_BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(dto)))
                .andExpect(status().isBadRequest());
    }
}
