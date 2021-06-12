package com.Udee.service;

import com.Udee.exception.notFound.ElectricMeterNotFoundException;
import com.Udee.models.*;
import com.Udee.models.projections.ElectricMeterProjection;
import com.Udee.repository.ElectricMeterRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ElectricMeterServiceTest {

    @Mock
    ElectricMeterRepository electricMeterRepository;
    @Mock
    ModelService modelService;
    @InjectMocks
    ElectricMeterService electricMeterService;
    ElectricMeter m;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        m = ElectricMeter.builder().id(1).serial("serial123").build();
    }

    @Test
    void testAddElectricMeter() {
        when(electricMeterRepository.save(any(ElectricMeter.class))).thenReturn(m);

        ElectricMeter result = electricMeterService.addElectricMeter(m);

        assertEquals(m.getSerial(), result.getSerial());
        verify(electricMeterRepository, times(1)).save(any());
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<ElectricMeter> p = new PageImpl<>(List.of(m), pageable, 1);
        Specification<ElectricMeter> spec = mock(Specification.class);
        when(electricMeterRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(p);

        Page<ElectricMeter> result = electricMeterService.findAll(spec, pageable);

        assertEquals(1, result.getTotalElements());
        verify(electricMeterRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testFindByIdFound() {
        when(electricMeterRepository.findById(anyInt())).thenReturn(Optional.of(m));

        ElectricMeter result = electricMeterService.findById(1);

        assertEquals(1, result.getId());
        verify(electricMeterRepository, times(1)).findById(anyInt());
    }

    @Test
    void testFindByIdNotFound() {
        when(electricMeterRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ElectricMeterNotFoundException.class, () -> electricMeterService.findById(1));

        verify(electricMeterRepository, times(1)).findById(anyInt());
    }

    @Test
    void testFindProjectionByIdFound() {
        ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
        when(electricMeterRepository.findProjectionById(anyInt()))
                .thenReturn(Optional.of(factory.createProjection(ElectricMeterProjection.class, m)));

        ElectricMeterProjection result = electricMeterService.findProjectionById(1);

        assertEquals(1, result.getId());
        verify(electricMeterRepository, times(1)).findProjectionById(anyInt());
    }

    @Test
    void testFindProjectionByIdNotFound() {
        when(electricMeterRepository.findProjectionById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(ElectricMeterNotFoundException.class, () -> electricMeterService.findProjectionById(1));

        verify(electricMeterRepository, times(1)).findProjectionById(anyInt());
    }

    @Test
    void testFindOneBySerialFound() {
        when(electricMeterRepository.findBySerial(anyString())).thenReturn(Optional.of(m));

        ElectricMeter result = electricMeterService.findOneBySerial(m.getSerial());

        assertEquals(m.getSerial(), result.getSerial());
        verify(electricMeterRepository, times(1)).findBySerial(anyString());
    }

    @Test
    void testFindOneBySerialNotFound() {
        when(electricMeterRepository.findBySerial(anyString())).thenReturn(Optional.empty());

        ElectricMeter result = electricMeterService.findOneBySerial(m.getSerial());

        assertNull(result);
        verify(electricMeterRepository, times(1)).findBySerial(anyString());
    }

    @Test
    void testSetModelToElectricMeter() {
        Integer meterId = 1;
        Integer modelId = 2;
        Model model = Model.builder().id(2).build();
        ElectricMeterService spy = spy(electricMeterService);
        doReturn(m).when(spy).findById(anyInt());
        when(modelService.findById(anyInt())).thenReturn(model);
        m.setModel(model);
        when(electricMeterRepository.save(any())).thenReturn(m);

        ElectricMeter result = spy.setModelToElectricMeter(meterId, modelId);

        assertEquals(modelId, result.getModel().getId());
        assertEquals(meterId, result.getId());
        verify(electricMeterRepository, times(1)).save(any());
    }

    @Test
    void testDelete() {
        doNothing().when(electricMeterRepository).deleteById(anyInt());

        electricMeterService.delete(1);
    }

    @Test
    void testDeleteNotFound() {
        doThrow(EmptyResultDataAccessException.class).when(electricMeterRepository).deleteById(anyInt());

        assertThrows(ElectricMeterNotFoundException.class, () -> electricMeterService.delete(1));
        verify(electricMeterRepository, times(1)).deleteById(anyInt());
    }
}

