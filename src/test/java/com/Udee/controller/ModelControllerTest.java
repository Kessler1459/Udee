package com.Udee.controller;

import com.Udee.PostResponse;
import com.Udee.models.*;
import com.Udee.models.dto.MessageDTO;
import com.Udee.models.dto.ModelDTO;
import com.Udee.service.ModelService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

class ModelControllerTest {
    @Mock
    ModelService modelService;
    @Mock
    Specification specification;

    ModelMapper modelMapper;

    ModelController modelController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        modelMapper = new ModelMapper();
        modelController = new ModelController(modelService, modelMapper);
    }

    @Test
    void testAddModel() {
        Model m = Model.builder().id(1).build();
        when(modelService.addModel(any())).thenReturn(m);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ResponseEntity<PostResponse> result = modelController.addModel(m);

        Assertions.assertEquals(HttpStatus.CREATED, result.getStatusCode());
        Assertions.assertEquals("http://localhost/api/back-office/electricmeters/brands/models/1", result.getBody().getUrl());
    }

    @Test
    void testFindAllHttp200() {
        Pageable pageable = PageRequest.of(0, 2);
        when(modelService.findAll(any(), any())).thenReturn(new PageImpl<>(List.of(new Model(), new Model()), pageable, 2));

        ResponseEntity<List<ModelDTO>> result = modelController.findAll(specification,pageable);

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertEquals(2, result.getBody().size());
    }

    @Test
    void testFindAllHttp204() {
        Pageable pageable = PageRequest.of(0, 2);
        when(modelService.findAll(any(), any())).thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 2));

        ResponseEntity<List<ModelDTO>> result = modelController.findAll(specification,pageable);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        Assertions.assertEquals(0, result.getBody().size());
    }

    @Test
    void testFindById() {
        when(modelService.findById(anyInt())).thenReturn(Model.builder().id(1).build());

        ResponseEntity<ModelDTO> result = modelController.findById(1);

        Assertions.assertEquals(1, result.getBody().getId());
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testDeleteModel() {
        doNothing().when(modelService).delete(anyInt());

        ResponseEntity<MessageDTO> result = modelController.deleteModel(1);

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}

