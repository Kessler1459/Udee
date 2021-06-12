package com.Udee.service;

import com.Udee.exception.notFound.ResidenceNotFoundException;
import com.Udee.models.*;
import com.Udee.models.projections.ResidenceProjection;
import com.Udee.repository.ResidenceRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResidenceServiceTest {
    @Mock
    ResidenceRepository residenceRepository;
    @Mock
    ElectricMeterService electricMeterService;
    @Mock
    RateService rateService;
    @Mock
    UserService userService;
    @InjectMocks
    ResidenceService residenceService;
    Residence res;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        res = Residence.builder().id(1).build();
    }

    @Test
    void testFindAll() {
        Specification<Residence> spec = mock(Specification.class);
        Pageable pageable = PageRequest.of(0, 1);
        when(residenceRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(res), pageable, 1));

        Page<Residence> result = residenceService.findAll(spec, pageable);

        assertEquals(1, result.getTotalElements());
        verify(residenceRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testAddResidence() {
        res.setAddress(new Address());
        when(residenceRepository.save(any(Residence.class))).thenReturn(res);

        Residence result = residenceService.addResidence(res);

        assertEquals(1, result.getId());
        verify(residenceRepository, times(1)).save(any(Residence.class));
    }

    @Test
    void testFindProjectionByIdFound() {
        ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
        when(residenceRepository.getById(anyInt()))
                .thenReturn(Optional.of(factory.createProjection(ResidenceProjection.class, res)));

        ResidenceProjection result = assertDoesNotThrow(() -> residenceService.findProjectionById(1));

        assertEquals(1, result.getId());
        verify(residenceRepository, times(1)).getById(anyInt());
    }

    @Test
    void testFindProjectionByIdNotFound() {
        when(residenceRepository.getById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(ResidenceNotFoundException.class, () -> residenceService.findProjectionById(1));

        verify(residenceRepository, times(1)).getById(anyInt());
    }

    @Test
    void testFindByIdFound() {
        when(residenceRepository.findById(anyInt()))
                .thenReturn(Optional.of(res));

        Residence result = assertDoesNotThrow(() -> residenceService.findById(1));

        assertEquals(1, result.getId());
        verify(residenceRepository, times(1)).findById(anyInt());

    }

    @Test
    void testFindByIdNotFound() {
        when(residenceRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(ResidenceNotFoundException.class, () -> residenceService.findById(1));

        verify(residenceRepository, times(1)).findById(anyInt());
    }

    @Test
    void testAddElectricMeterNotThrows() {
        when(electricMeterService.findById(anyInt()))
                .thenReturn(ElectricMeter.builder().id(2).build());
        ResidenceService spy = spy(residenceService);
        doReturn(res).when(spy).findById(anyInt());
        when(residenceRepository.save(res)).thenReturn(res);

        Residence result = assertDoesNotThrow(() -> spy.addElectricMeter(1, 2));

        assertEquals(2, result.getElectricMeter().getId());
        verify(residenceRepository, times(1)).save(any(Residence.class));
    }

    @Test
    void testAddElectricMeterThrows() {
        when(electricMeterService.findById(anyInt()))
                .thenReturn(ElectricMeter.builder().id(2).residence(res).build());
        ResidenceService spy = spy(residenceService);
        doReturn(res).when(spy).findById(anyInt());
        when(residenceRepository.save(res)).thenReturn(res);

        assertThrows(HttpClientErrorException.class, () -> spy.addElectricMeter(1, 2));

        verify(residenceRepository, times(0)).save(any(Residence.class));
    }


    @Test
    void testAddRate() {
        Rate rate = Rate.builder().id(2).build();
        when(rateService.findById(anyInt())).thenReturn(rate);
        when(residenceRepository.save(res)).thenReturn(res);
        ResidenceService spy = spy(residenceService);
        doReturn(res).when(spy).findById(anyInt());

        Residence result = spy.addRate(1, 2);

        assertEquals(1, result.getId());
        assertEquals(2, result.getRate().getId());
        verify(residenceRepository, times(1)).save(any(Residence.class));
    }

    @Test
    void testAddUser() {
        User user = User.builder().id(2).build();
        when(userService.findById(anyInt())).thenReturn(user);
        when(residenceRepository.save(res)).thenReturn(res);
        ResidenceService spy = spy(residenceService);
        doReturn(res).when(spy).findById(anyInt());

        Residence result = spy.addUser(1, 2);

        assertEquals(1, result.getId());
        assertEquals(2, result.getUser().getId());
        verify(residenceRepository, times(1)).save(any(Residence.class));
    }

    @Test
    void testDeleteNotThrows() {
        doNothing().when(residenceRepository).deleteById(anyInt());

        assertDoesNotThrow(() -> residenceService.delete(1));

        verify(residenceRepository, times(1)).deleteById(anyInt());
    }

    @Test
    void testDeleteThrows() {
        doThrow(EmptyResultDataAccessException.class).when(residenceRepository).deleteById(anyInt());

        assertThrows(ResidenceNotFoundException.class, () -> residenceService.delete(1));

        verify(residenceRepository, times(1)).deleteById(anyInt());
    }
}

