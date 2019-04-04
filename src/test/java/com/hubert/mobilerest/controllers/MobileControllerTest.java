package com.hubert.mobilerest.controllers;

import com.hubert.mobilerest.domain.ServiceType;
import com.hubert.mobilerest.dto.v1.MobileSubscriberDto;
import com.hubert.mobilerest.dto.v1.MobileSubscribersDto;
import com.hubert.mobilerest.exceptions.ResourceNotFoundException;
import com.hubert.mobilerest.exceptions.ValidationFailedException;
import com.hubert.mobilerest.mappers.MobileSubscriberMapper;
import com.hubert.mobilerest.services.MobileSubscriberService;
import com.hubert.mobilerest.utils.TestUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
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

    @Autowired
    MockMvc mockMvc;

    private MobileSubscriberDto subscriber;
    private MobileSubscriberDto subscriber2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        subscriber = MobileSubscriberDto.builder().userId(1L).ownerId(2L).msisdn("48123312123")
                .serviceType(ServiceType.MOBILE_POSTPAID.name()).serviceStartDate(123000000L).build();

        subscriber2 = MobileSubscriberDto.builder().userId(4L).ownerId(3L).msisdn("48987789987")
                .serviceType(ServiceType.MOBILE_PREPAID.name()).serviceStartDate(124000000L).build();
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
        given(service.findSubscribersByCriteria(MobileSubscriberDto.builder().build()))
                .willReturn(new MobileSubscribersDto(Lists.newArrayList(subscriber, subscriber2)));

        //when/then
        mockMvc.perform(get(CONTROLLER_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subscribers", hasSize(2)));
    }

    @Test
    void shouldFindByCriteriaTest() throws Exception {
        given(service.findSubscribersByCriteria(
                MobileSubscriberDto.builder()
                        .msisdn("123123123")
                        .serviceType("MOBILE_POSTPAID")
                        .serviceStartDate(13123213L)
                        .userId(1L)
                        .ownerId(2L)
                        .build()))
                .willReturn(new MobileSubscribersDto(Lists.newArrayList(subscriber, subscriber2)));

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
        MobileSubscriberDto dto = MobileSubscriberDto.builder()
                .serviceType("MOBILE_PREPAID")
                .msisdn("49123123123")
                .ownerId(1L)
                .userId(2L)
                .build();

        MobileSubscriberDto returnedDto = MobileSubscriberDto.builder()
                .serviceType("MOBILE_PREPAID")
                .msisdn("49123123123")
                .ownerId(1L)
                .userId(2L)
                .serviceStartDate(1323132123L)
                .build();

        given(service.createNewSubscriber(dto)).willReturn(returnedDto);

        //when/then
        mockMvc.perform(post(CONTROLLER_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.msisdn", is("49123123123")))
                .andExpect(jsonPath("$.serviceStartDate", is(1323132123)));
    }

    @Test
    void shouldUpdateSubscriberTest() throws Exception {
        //given
        MobileSubscriberDto dto = MobileSubscriberDto.builder()
                .serviceType("MOBILE_PREPAID")
                .msisdn("49123123123")
                .ownerId(1L)
                .userId(2L)
                .build();

        MobileSubscriberDto returnedDto = MobileSubscriberDto.builder()
                .serviceType("MOBILE_PREPAID")
                .msisdn("49123123123")
                .ownerId(1L)
                .userId(2L)
                .serviceStartDate(1323132123L)
                .build();

        given(service.updateSubscriber(dto, 1L)).willReturn(returnedDto);

        //when/then
        mockMvc.perform(put(CONTROLLER_BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msisdn", is("49123123123")))
                .andExpect(jsonPath("$.serviceStartDate", is(1323132123)));
    }

    @Test
    void shouldPatchSubscriberTest() throws Exception {
        //given
        MobileSubscriberDto dto = MobileSubscriberDto.builder()
                .ownerId(1L)
                .userId(2L)
                .build();

        MobileSubscriberDto returnedDto = MobileSubscriberDto.builder()
                .serviceType("MOBILE_PREPAID")
                .msisdn("49123123123")
                .ownerId(1L)
                .userId(2L)
                .serviceStartDate(1323132123L)
                .build();

        given(service.patchSubscriber(dto, 1L)).willReturn(returnedDto);

        //when/then
        mockMvc.perform(patch(CONTROLLER_BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msisdn", is("49123123123")))
                .andExpect(jsonPath("$.serviceStartDate", is(1323132123)));
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

        given(service.patchSubscriber(Mockito.any(), anyLong())).willThrow(ValidationFailedException.class);

        //when/then
        mockMvc.perform(patch(CONTROLLER_BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(dto)))
                .andExpect(status().isBadRequest());
    }
}
