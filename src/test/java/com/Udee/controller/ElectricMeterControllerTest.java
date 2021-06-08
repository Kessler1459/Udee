package com.Udee.controller;

import com.Udee.PostResponse;
import com.Udee.models.ElectricMeter;
import com.Udee.models.Model;
import com.Udee.models.dto.ElectricMeterDTO;
import com.Udee.models.dto.MessageDTO;
import com.Udee.models.projections.BillProjection;
import com.Udee.models.projections.ElectricMeterProjection;
import com.Udee.service.ElectricMeterService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ElectricMeterControllerTest {
    @Mock
    ElectricMeterService electricMeterService;

    @Mock
    PasswordEncoder passwordEncoder;

    ModelMapper modelMapper;
    ElectricMeterController electricMeterController;
    List<ElectricMeter> meters;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        modelMapper = new ModelMapper();
        electricMeterController=new ElectricMeterController(electricMeterService,modelMapper,passwordEncoder);
        meters = List.of(ElectricMeter.builder().id(1).serial("as1212").pass("passwewewe").build(),
                ElectricMeter.builder().id(2).serial("as1213").pass("passwewewe").build());
    }

    @Test
    void testAddElectricMeter() {
        ElectricMeter em = ElectricMeter.builder().id(1).serial("as1212").pass("passwewewe").build();
        when(electricMeterService.addElectricMeter(em)).thenReturn(em);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        PostResponse pr = new PostResponse("http://localhost/api/back-office/electricmeters/" + em.getId(), HttpStatus.CREATED.getReasonPhrase());

        ResponseEntity<PostResponse> result = electricMeterController.addElectricMeter(em);

        Assertions.assertEquals(pr, result.getBody());
        Assertions.assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void testFindAllHttp200() {
        Pageable pageable= PageRequest.of(0,2);
        Specification spec=mock(Specification.class);
        Page<ElectricMeter> page= new PageImpl<>(meters,pageable,2);
        when(electricMeterService.findAll(any(), any())).thenReturn(page);

        ResponseEntity<List<ElectricMeterDTO>> result = electricMeterController.findAll(spec,pageable);

        Assertions.assertEquals(meters.get(0).getId(), result.getBody().get(0).getId());
        Assertions.assertEquals(meters.size(),result.getBody().size());
        Assertions.assertEquals(HttpStatus.OK,result.getStatusCode());
        verify(electricMeterService,times(1)).findAll(spec,pageable);
        assertEquals("2", result.getHeaders().get("X-totalElements").get(0));
        assertEquals("1", result.getHeaders().get("X-totalPages").get(0));
    }

    @Test
    void testFindAllHttp204() {
        Pageable pageable= PageRequest.of(0,2);
        Specification spec=mock(Specification.class);
        Page<ElectricMeter> page= new PageImpl<>(Collections.emptyList(),pageable,0);
        when(electricMeterService.findAll(any(), any())).thenReturn(page);

        ResponseEntity<List<ElectricMeterDTO>> result = electricMeterController.findAll(spec,pageable);

        Assertions.assertEquals(Collections.emptyList(), result.getBody());
        Assertions.assertEquals(HttpStatus.NO_CONTENT,result.getStatusCode());
        verify(electricMeterService,times(1)).findAll(spec,pageable);
        assertEquals("0", result.getHeaders().get("X-totalElements").get(0));
        assertEquals("0", result.getHeaders().get("X-totalPages").get(0));
    }

    @Test
    void testFindById() {
        ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
        ElectricMeterProjection projection = factory.createProjection(ElectricMeterProjection.class);
        projection.setId(1);
        when(electricMeterService.findProjectionById(anyInt())).thenReturn(projection);

        ResponseEntity<ElectricMeterProjection> result = electricMeterController.findById(1);

        Assertions.assertEquals(projection, result.getBody());
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(electricMeterService,times(1)).findProjectionById(1);
    }

    @Test
    void testSetModelToElectricMeter() {
        Model m=Model.builder().id(2).build();
        ElectricMeter em = ElectricMeter.builder().id(1).serial("as1212").pass("passwewewe").model(m).build();
        when(electricMeterService.setModelToElectricMeter(anyInt(),anyInt())).thenReturn(em);

        ResponseEntity<ElectricMeterDTO> result = electricMeterController.setModelToElectricMeter(1, 2);

        Assertions.assertEquals(em.getId(), result.getBody().getId());
        Assertions.assertEquals(m.getId(), result.getBody().getModel().getId());
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testDeleteMeter() {
        doNothing().when(electricMeterService).delete(anyInt());

        ResponseEntity<MessageDTO> result = electricMeterController.deleteMeter(1);

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(electricMeterService,times(1)).delete(anyInt());
    }
}

