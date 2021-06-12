package com.Udee.service;

import com.Udee.exception.notFound.RateNotFoundException;
import com.Udee.models.*;
import com.Udee.repository.RateRepository;
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

class RateServiceTest {
    @Mock
    RateRepository rateRepository;
    @InjectMocks
    RateService rateService;
    Rate rate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rate = Rate.builder().id(1).name("rate1").priceXKW(2f).build();
    }

    @Test
    void testAddRate() {
        when(rateRepository.save(any(Rate.class))).thenReturn(rate);

        Rate result = rateService.addRate(rate);

        assertEquals(rate.getId(), result.getId());
        verify(rateRepository, times(1)).save(any(Rate.class));
    }

    @Test
    void testFindAll() {
        Specification<Rate> spec = mock(Specification.class);
        Pageable pageable = PageRequest.of(0, 1);
        when(rateRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(rate), pageable, 1));

        Page<Rate> result = rateService.findAll(spec, pageable);

        assertEquals(1, result.getTotalElements());
        verify(rateRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testFindByIdFound() {
        when(rateRepository.findById(anyInt())).thenReturn(Optional.of(rate));

        Rate result = assertDoesNotThrow(() -> rateService.findById(1));

        assertEquals(1, result.getId());
        verify(rateRepository, times(1)).findById(anyInt());
    }

    @Test
    void testFindByIdNotFound() {
        when(rateRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(RateNotFoundException.class, () -> rateService.findById(1));

        verify(rateRepository, times(1)).findById(anyInt());
    }

    @Test
    void testDeleteRateNotThrows() {
        doNothing().when(rateRepository).deleteById(anyInt());

        assertDoesNotThrow(() -> rateService.deleteRate(1));

        verify(rateRepository, times(1)).deleteById(anyInt());
    }

    @Test
    void testDeleteRateThrows() {
        doThrow(EmptyResultDataAccessException.class).when(rateRepository).deleteById(anyInt());

        assertThrows(RateNotFoundException.class, () -> rateService.deleteRate(1));

        verify(rateRepository, times(1)).deleteById(anyInt());
    }

    @Test
    void testUpdateRate() {
        when(rateRepository.save(any(Rate.class))).thenReturn(rate);

        Rate result = rateService.updateRate(rate);

        assertEquals(rate.getId(), result.getId());
        verify(rateRepository, times(1)).save(any(Rate.class));
    }
}

