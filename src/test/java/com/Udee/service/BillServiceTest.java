package com.Udee.service;

import com.Udee.exception.notFound.BillNotFoundException;
import com.Udee.models.Bill;
import com.Udee.models.projections.BillProjection;
import com.Udee.repository.BillRepository;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class BillServiceTest {
    @Mock
    BillRepository billRepository;

    BillService billService;
    List<Bill> billList;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        billService = new BillService(billRepository);
        billList = List.of(Bill.builder().id(1).date(LocalDate.of(2021, 1, 1)).usage(20).build()
                , Bill.builder().id(1).date(LocalDate.of(2021, 2, 1)).usage(100).build());
    }

    @Test
    public void testFindAll() {
        Specification<Bill> spec = mock(Specification.class);
        Pageable pageable = mock(Pageable.class);
        Page<Bill> billsP = new PageImpl<>(billList, pageable, 2);
        when(billRepository.findAll(spec, pageable)).thenReturn(billsP);

        Page<Bill> result = billService.findAll(spec, pageable);

        assertEquals(billsP, result);
        verify(billRepository, times(1)).findAll(spec, pageable);
    }

    @Test
    public void testFindByIdFound() {
        Integer id = 1;
        when(billRepository.findById(id)).thenReturn(Optional.of(Bill.builder().id(1).date(LocalDate.of(2021, 1, 1)).usage(20).build()));

        Bill found=assertDoesNotThrow(() -> billService.findById(id));
        assertEquals(id,found.getId());
        verify(billRepository,times(1)).findById(id);
    }

    @Test
    public void testFindByIdNotFound() {
        Integer id = 1;
        when(billRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(BillNotFoundException.class,() -> billService.findById(id));
        verify(billRepository,times(1)).findById(id);
    }

    @Test
    public void testFindProjectedByIdFound(){
        Integer id=1;
        ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
        BillProjection projection = factory.createProjection(BillProjection.class);
        projection.setId(id);
        when(billRepository.findProjectionById(id)).thenReturn(Optional.of(projection));

        BillProjection billProjection=assertDoesNotThrow(() -> billService.findProjectedById(id));
        assertEquals(projection,billProjection);
        verify(billRepository,times(1)).findProjectionById(id);
    }

    @Test
    public void testFindProjectedByIdNotFound(){
        Integer id=1;
        ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
        BillProjection projection = factory.createProjection(BillProjection.class);
        projection.setId(id);
        when(billRepository.findProjectionById(id)).thenReturn(Optional.empty());

        assertThrows(BillNotFoundException.class,() -> billService.findProjectedById(id));
        verify(billRepository,times(1)).findProjectionById(id);
    }
}
