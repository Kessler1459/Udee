package com.Udee.controller;

import com.Udee.PostResponse;
import com.Udee.exception.WrongCredentialsException;
import com.Udee.models.*;
import com.Udee.models.dto.MeasureDTO;
import com.Udee.models.dto.MeasureRDTO;
import com.Udee.models.dto.UsageDTO;
import com.Udee.models.dto.UserDTO;
import com.Udee.models.projections.UserRank;
import com.Udee.service.ElectricMeterService;
import com.Udee.service.MeasureService;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

class MeasureControllerTest {
    @Mock
    MeasureService measureService;
    @Mock
    ElectricMeterService electricMeterService;
    @Mock
    ResidenceService residenceService;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    Authentication authentication;
    @Mock
    Specification spec;
    ModelMapper modelMapper;
    MeasureController measureController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        modelMapper=new ModelMapper();
        measureController = new MeasureController(measureService, electricMeterService, residenceService, modelMapper, passwordEncoder);
    }

    @Test
    void testAddMeasureMeterExistsPasswordMatches() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        MeasureRDTO dto = MeasureRDTO.builder().password("asdasdasd").date(LocalDateTime.now().toString()).serialNumber("serial343434").value(100f).build();
        Measure m = Measure.builder().measure((int) dto.getValue()).id(BigInteger.ONE).dateTime(LocalDateTime.parse(dto.getDate())).build();
        when(electricMeterService.findOneBySerial(anyString())).thenReturn(ElectricMeter.builder().serial("serial343434").build());
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(measureService.addMeasure(any())).thenReturn(m);

        ResponseEntity<PostResponse> result = Assertions.assertDoesNotThrow(() ->
                measureController.addMeasure(dto));

        Assertions.assertEquals("http://localhost/api/measures/1", result.getBody().getUrl());
        Assertions.assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void testAddMeasureMeterExistsPasswordNotMatches() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        MeasureRDTO dto = MeasureRDTO.builder().password("asdasdasd").date("2020-01-01T00:00:00").serialNumber("serial343434").value(100f).build();
        when(electricMeterService.findOneBySerial(anyString())).thenReturn(ElectricMeter.builder().serial("serial343434").build());
        when(passwordEncoder.matches(any(), any())).thenReturn(false);


        Assertions.assertThrows(WrongCredentialsException.class,() ->  measureController.addMeasure(dto));
    }

    @Test
    void testAddMeasureMeterNotExistsPasswordMatches() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        MeasureRDTO dto = MeasureRDTO.builder().password("asdasdasd").date("2020-01-01T00:00:00").serialNumber("serial343434").value(100f).build();
        when(electricMeterService.findOneBySerial(anyString())).thenReturn(null);

        Assertions.assertThrows(WrongCredentialsException.class,() ->  measureController.addMeasure(dto));
    }

    @Test
    void testFindUsageBetweenDatesByResidenceOwned() {
        when(measureService.findUsageBetweenDatesByResidence(anyInt(), any(), any())).thenReturn(new UsageDTO(100,200f));
        when(authentication.getPrincipal()).thenReturn(UserDTO.builder().id(1).build());
        when(residenceService.findById(anyInt())).thenReturn(Residence.builder().user(User.builder().id(1).build()).build());

        ResponseEntity<UsageDTO> result = measureController.findUsageBetweenDatesByResidence(1,LocalDate.parse("2020-01-01",DateTimeFormatter.ISO_LOCAL_DATE),LocalDate.parse("2021-01-01",DateTimeFormatter.ISO_LOCAL_DATE),authentication);

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testFindUsageBetweenDatesByResidenceNotOwned() {
        Authentication authentication=mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(UserDTO.builder().id(2).build());
        when(residenceService.findById(anyInt())).thenReturn(Residence.builder().user(User.builder().id(1).build()).build());

        Assertions.assertThrows(AccessDeniedException.class,() ->measureController.findUsageBetweenDatesByResidence(1,LocalDate.parse("2020-01-01",DateTimeFormatter.ISO_LOCAL_DATE),LocalDate.parse("2021-01-01",DateTimeFormatter.ISO_LOCAL_DATE),authentication) );
    }

    @Test
    void testFindUsageBetweenDatesByClient() {
        when(measureService.findUsageBetweenDatesByClient(anyInt(), any(), any())).thenReturn(new UsageDTO(100,200f));

        ResponseEntity<UsageDTO> result = measureController.findUsageBetweenDatesByClient(1,LocalDate.parse("2020-01-01",DateTimeFormatter.ISO_LOCAL_DATE),LocalDate.parse("2021-01-01",DateTimeFormatter.ISO_LOCAL_DATE),authentication);
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testFindMeasuresBetweenDatesOwnedHttp200() {
        Pageable pageable= PageRequest.of(0,2);
        Page<Measure> p=new PageImpl<>(List.of(new Measure(),new Measure()),pageable,2);
        when(authentication.getPrincipal()).thenReturn(UserDTO.builder().id(1).build());
        when(residenceService.findById(anyInt())).thenReturn(Residence.builder().user(User.builder().id(1).build()).build());
        when(measureService.findAll(spec,pageable)).thenReturn(p);

        ResponseEntity<List<MeasureDTO>> result = measureController.findMeasuresBetweenDates(1,pageable,spec,authentication);

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertEquals(2,result.getBody().size());
    }

    @Test
    void testFindMeasuresBetweenDatesOwnedHttp204() {
        Pageable pageable= PageRequest.of(0,2);
        Page<Measure> p=new PageImpl<>(Collections.emptyList(),pageable,0);
        when(authentication.getPrincipal()).thenReturn(UserDTO.builder().id(1).build());
        when(residenceService.findById(anyInt())).thenReturn(Residence.builder().user(User.builder().id(1).build()).build());
        when(measureService.findAll(spec,pageable)).thenReturn(p);

        ResponseEntity<List<MeasureDTO>> result = measureController.findMeasuresBetweenDates(1,pageable,spec,authentication);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        Assertions.assertEquals(0,result.getBody().size());
    }

    @Test
    void testFindMeasuresBetweenDatesNotOwned() {
        Pageable pageable= PageRequest.of(0,2);
        when(authentication.getPrincipal()).thenReturn(UserDTO.builder().id(1).build());
        when(residenceService.findById(anyInt())).thenReturn(Residence.builder().user(User.builder().id(2).build()).build());

        Assertions.assertThrows(AccessDeniedException.class,() -> measureController.findMeasuresBetweenDates(1,pageable,spec,authentication));
    }

    @Test
    void testGetTopTenUsersHttp200() {
        UserRank u=mock(UserRank.class);
        when(measureService.findRankBetweenDates(any(), any())).thenReturn(List.of(u,u));

        ResponseEntity<List<UserRank>> result = measureController.getTopTenUsers(LocalDate.of(2021, Month.JUNE, 10), LocalDate.of(2021, Month.JUNE, 10));

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertEquals(2, result.getBody().size());
    }

    @Test
    void testGetTopTenUsersHttp204() {
        when(measureService.findRankBetweenDates(any(), any())).thenReturn(Collections.emptyList());

        ResponseEntity<List<UserRank>> result = measureController.getTopTenUsers(LocalDate.of(2021, Month.JUNE, 10), LocalDate.of(2021, Month.JUNE, 10));

        Assertions.assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        Assertions.assertEquals(0, result.getBody().size());
    }

    @Test
    void testFindById(){
        when(measureService.findById(1)).thenReturn(new Measure());

        ResponseEntity<MeasureDTO> result = measureController.findMeasureById(1);

        Assertions.assertEquals(HttpStatus.OK,result.getStatusCode());
        Assertions.assertEquals(new MeasureDTO(),result.getBody());
    }
}

