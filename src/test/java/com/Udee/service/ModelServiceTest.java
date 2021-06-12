package com.Udee.service;

import com.Udee.exception.notFound.ModelNotFoundException;
import com.Udee.models.*;
import com.Udee.repository.ModelRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ModelServiceTest {
    @Mock
    ModelRepository modelRepository;
    @InjectMocks
    ModelService modelService;
    Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        model = Model.builder().id(1).name("model1").build();
    }

    @Test
    void testFindByIdFound() {
        when(modelRepository.findById(anyInt()))
                .thenReturn(Optional.of(model));

        Model result = assertDoesNotThrow(() -> modelService.findById(1));

        assertEquals(model, result);
        verify(modelRepository, times(1)).findById(anyInt());
    }

    @Test
    void testFindByIdNotFound() {
        when(modelRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(ModelNotFoundException.class, () -> modelService.findById(1));

        verify(modelRepository, times(1)).findById(anyInt());
    }

    @Test
    void testAddModel() {
        when(modelRepository.save(any(Model.class))).thenReturn(model);

        Model result = modelService.addModel(model);

        assertEquals(model, result);
        verify(modelRepository, times(1)).save(any(Model.class));
    }

    @Test
    void testFindAll() {
        Specification<Model> spec = mock(Specification.class);
        Pageable pageable = PageRequest.of(0, 1);
        when(modelRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(model), pageable, 1));

        Page<Model> result = modelService.findAll(spec, pageable);

        assertEquals(1, result.getTotalElements());
        verify(modelRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testDeleteNotThrows() {
        doNothing().when(modelRepository).deleteById(anyInt());

        assertDoesNotThrow(() -> modelService.delete(1));

        verify(modelRepository, times(1)).deleteById(anyInt());
    }

    @Test
    void testDeleteThrows() {
        doThrow(EmptyResultDataAccessException.class).when(modelRepository).deleteById(anyInt());

        assertThrows(ModelNotFoundException.class, () -> modelService.delete(1));

        verify(modelRepository, times(1)).deleteById(anyInt());
    }
}

