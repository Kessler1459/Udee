package com.Udee.controller;

import com.Udee.PostResponse;
import com.Udee.models.*;
import com.Udee.models.dto.MessageDTO;
import com.Udee.models.dto.ResidenceDTO;
import com.Udee.models.projections.ResidenceProjection;
import com.Udee.service.ResidenceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

class ResidenceControllerTest {
    @Mock
    ResidenceService residenceService;

    ModelMapper modelMapper;
    Residence r;
    ResidenceController residenceController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        modelMapper=new ModelMapper();
        residenceController=new ResidenceController(residenceService,modelMapper);
        r=Residence.builder().id(1).build();
    }

    @Test
    void testFindAllHttp200() {
        Specification spec=mock(Specification.class);
        Pageable pageable= PageRequest.of(0,2);
        Page<Residence> p = new PageImpl<>(List.of(new Residence(),new Residence()),pageable,2);
        when(residenceService.findAll(any(),any())).thenReturn(p);

        ResponseEntity<List<ResidenceDTO>> result = residenceController.findAll(pageable,spec);

        Assertions.assertEquals(HttpStatus.OK,result.getStatusCode());
        Assertions.assertEquals(2, result.getBody().size());
    }

    @Test
    void testFindAllHttp204() {
        Specification spec=mock(Specification.class);
        Pageable pageable= PageRequest.of(0,2);
        Page<Residence> p = new PageImpl<>(Collections.emptyList(),pageable,0);
        when(residenceService.findAll(any(),any())).thenReturn(p);

        ResponseEntity<List<ResidenceDTO>> result = residenceController.findAll(pageable,spec);

        Assertions.assertEquals(HttpStatus.NO_CONTENT,result.getStatusCode());
        Assertions.assertEquals(0, result.getBody().size());
    }

    @Test
    void testAddResidence() {

        when(residenceService.addResidence(any())).thenReturn(r);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ResponseEntity<PostResponse> result = residenceController.addResidence(r);

        Assertions.assertEquals("http://localhost/api/back-office/residences/1", result.getBody().getUrl());
        Assertions.assertEquals(HttpStatus.CREATED,result.getStatusCode());
    }

    @Test
    void testFindById() {
        ProjectionFactory factory=new SpelAwareProxyProjectionFactory();
        ResidenceProjection projection=factory.createProjection(ResidenceProjection.class,r);
        when(residenceService.findProjectionById(anyInt())).thenReturn(projection);

        ResponseEntity<ResidenceProjection> result=residenceController.findById(1);

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertEquals(1, result.getBody().getId());
    }

    @Test
    void testDeleteResidence() {
        doNothing().when(residenceService).delete(anyInt());

        ResponseEntity<MessageDTO> result = residenceController.deleteResidence(1);

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testAddElectricMeter() {
        r.setElectricMeter(ElectricMeter.builder().id(1).build());
        when(residenceService.addElectricMeter(anyInt(), anyInt())).thenReturn(r);

        ResponseEntity<ResidenceDTO> result = residenceController.addElectricMeter(1, 1);

        Assertions.assertEquals(1, result.getBody().getId());
        Assertions.assertEquals(1, result.getBody().getElectricMeter().getId());
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testAddRate() {
        r.setRate(Rate.builder().id(1).build());
        when(residenceService.addRate(anyInt(), anyInt())).thenReturn(r);

        ResponseEntity<ResidenceDTO> result = residenceController.addRate(1, 1);

        Assertions.assertEquals(1, result.getBody().getId());
        Assertions.assertEquals(1, result.getBody().getRate().getId());
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testAddUser() {
        r.setUser(User.builder().id(1).build());
        when(residenceService.addUser(anyInt(), anyInt())).thenReturn(r);

        ResponseEntity<ResidenceDTO> result = residenceController.addUser(1, 1);

        Assertions.assertEquals(1, result.getBody().getId());
        Assertions.assertEquals(1, result.getBody().getUser().getId());
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}

