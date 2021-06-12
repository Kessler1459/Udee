package com.Udee.service;

import com.Udee.exception.notFound.BrandNotFoundException;
import com.Udee.exception.notFound.ElectricMeterNotFoundException;
import com.Udee.models.Brand;
import com.Udee.models.ElectricMeter;
import com.Udee.models.Model;
import com.Udee.repository.BrandRepository;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BrandServiceTest {
    @Mock
    BrandRepository brandRepository;
    @Mock
    ModelService modelService;

    @InjectMocks
    BrandService brandService;
    Brand b;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        b = Brand.builder().id(1).name("marca1").build();
    }

    @Test
    void testFindAll() {
        Specification<Brand> spec = mock(Specification.class);
        Pageable pageable = PageRequest.of(0, 1);
        when(brandRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(b), pageable, 1));

        Page<Brand> result = brandService.findAll(spec, pageable);

        assertEquals(1, result.getTotalElements());
        verify(brandRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testAddBrand() {
        when(brandRepository.save(any(Brand.class))).thenReturn(b);

        Brand result = brandService.addBrand(b);

        assertEquals(b.getName(), result.getName());
        verify(brandRepository, times(1)).save(any(Brand.class));
    }

    @Test
    void testFindByIdFound() {
        when(brandRepository.findById(anyInt())).thenReturn(Optional.of(b));

        Brand result = brandService.findById(1);

        assertEquals(1, result.getId());
        verify(brandRepository, times(1)).findById(anyInt());
    }

    @Test
    void testFindByIdNotFound() {
        when(brandRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(BrandNotFoundException.class, () -> brandService.findById(1));

        verify(brandRepository, times(1)).findById(anyInt());
    }

    @Test
    void testAddModelToBrand() {
        Model m = Model.builder().id(2).build();
        b.addModel(m);
        BrandService spy=spy(brandService);
        doReturn(b).when(spy).findById(1);
        when(modelService.findById(2)).thenReturn(m);
        when(brandRepository.save(any(Brand.class))).thenReturn(b);

        Brand result = spy.addModelToBrand(1, 2);

        assertEquals(1, result.getId());
        assertEquals(2, result.getModels().get(0).getId());
        verify(brandRepository, times(1)).save(any(Brand.class));
    }

    @Test
    void testDeleteNotThrows() {
        doNothing().when(brandRepository).deleteById(anyInt());

        assertDoesNotThrow(() -> brandService.delete(1));

        verify(brandRepository, times(1)).deleteById(anyInt());
    }

    @Test
    void testDeleteThrows() {
        doThrow(EmptyResultDataAccessException.class).when(brandRepository).deleteById(anyInt());

        assertThrows(ElectricMeterNotFoundException.class,() -> brandService.delete(1));

        verify(brandRepository, times(1)).deleteById(anyInt());
    }
}

