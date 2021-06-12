package com.Udee.service;

import com.Udee.exception.notFound.MeasureNotFoundException;
import com.Udee.models.*;
import com.Udee.models.dto.UsageDTO;
import com.Udee.models.projections.UserRank;
import com.Udee.repository.MeasureRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class MeasureServiceTest {
    @Mock
    MeasureRepository measureRepository;
    @Mock
    ResidenceService residenceService;
    @Mock
    UserService userService;
    @InjectMocks
    MeasureService measureService;
    Measure measure;

    @BeforeEach
    void setUp() {
        openMocks(this);
        measure = Measure.builder().id(BigInteger.ONE).measure(20).usage(20).price(20f).dateTime(LocalDateTime.now()).build();
    }

    @Test
    void testAddMeasure() {
        when(measureRepository.save(any(Measure.class))).thenReturn(measure);

        Measure result = measureService.addMeasure(measure);

        assertEquals(measure, result);
        verify(measureRepository, times(1)).save(any(Measure.class));
    }

    @Test
    void testFindAll() {
        Specification<Measure> spec = mock(Specification.class);
        Pageable pageable = PageRequest.of(0, 1);
        when(measureRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(measure), pageable, 1));

        Page<Measure> result = measureService.findAll(spec, pageable);

        assertEquals(1, result.getTotalElements());
        verify(measureRepository, times(1)).findAll(spec, pageable);
    }

    @Test
    void testFindUsageBetweenDatesByResidence() {
        Residence r = Residence.builder().id(2).electricMeter(ElectricMeter.builder().id(3).build()).build();
        when(residenceService.findById(anyInt())).thenReturn(r);
        when(measureRepository.findAllByElectricMeterBetweenDates(anyInt(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(measure));

        UsageDTO result = measureService.findUsageBetweenDatesByResidence(2, LocalDate.of(2021, Month.JUNE, 12), LocalDate.of(2021, Month.JUNE, 16));

        assertNotNull(result);
        assertEquals(measure.getUsage(), result.getUsage());
        verify(measureRepository, times(1)).findAllByElectricMeterBetweenDates(anyInt(), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void testFindUsageBetweenDatesByClient() {
        User u = User.builder().id(2).build();
        when(userService.findById(anyInt())).thenReturn(u);
        when(measureRepository.findAllByUserBetweenDates(anyInt(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(measure));

        UsageDTO result = measureService.findUsageBetweenDatesByClient(2, LocalDate.of(2021, Month.JUNE, 12), LocalDate.of(2021, Month.JUNE, 16));

        assertNotNull(result);
        assertEquals(measure.getUsage(), result.getUsage());
        verify(measureRepository, times(1)).findAllByUserBetweenDates(anyInt(), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void testGetUsageDTO() {
        List<Measure> list = List.of(measure);
        MeasureService spy = spy(measureService);

        UsageDTO result = spy.getUsageDTO(list);

        assertEquals(measure.getUsage(), result.getUsage());
        assertEquals(measure.getPrice(), result.getTotalCost());
    }

    @Test
    void testFindRankBetweenDates() {
        UserRank rank = mock(UserRank.class);
        when(measureRepository.findRankBetweenDates(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(rank));

        List<UserRank> result = measureService.findRankBetweenDates(LocalDate.of(2021, Month.JUNE, 12), LocalDate.of(2021, Month.JUNE, 12));

        assertEquals(1, result.size());
        verify(measureRepository, times(1)).findRankBetweenDates(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void testFindByIdFound() {
        when(measureRepository.findById(any(BigInteger.class)))
                .thenReturn(Optional.of(measure));

        Measure result = assertDoesNotThrow(() -> measureService.findById(1));

        assertEquals(BigInteger.ONE, result.getId());
        verify(measureRepository, times(1)).findById(any(BigInteger.class));
    }

    @Test
    void testFindByIdNotFound() {
        when(measureRepository.findById(any(BigInteger.class)))
                .thenReturn(Optional.empty());

        assertThrows(MeasureNotFoundException.class,() -> measureService.findById(1));

        verify(measureRepository, times(1)).findById(any(BigInteger.class));
    }
}

