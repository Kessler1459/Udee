package com.Udee.controllers;

import com.Udee.AbstractController;
import com.Udee.models.Bill;
import com.Udee.services.BillService;
import com.Udee.services.ResidenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.openMocks;

public class BillControllerTest extends AbstractController {

    @Mock
    BillService billService;

    @Mock
    ResidenceService residenceService;
    ConversionService conversionService;
    BillController billController;
    List<Bill> billList;

    @BeforeEach
    void setUp() {
        openMocks(this);
        //billController = new BillController(billService, conversionService, residenceService, modelMapper);

        billList= List.of(Bill.builder().id(1).date(LocalDate.of(2021, 1, 1)).usage(20).build()
                , Bill.builder().id(1).date(LocalDate.of(2021, 2, 1)).usage(100).build());
    }

    @Test
    public void testFindAllByUserHttpStatus200() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Bill> page = mock(Page.class);


        //ResponseEntity<List<BillDTO>> response=ResponseEntity

    }
/*
    @Test
    public void testGetListResponseEntityFilled() {
        Pageable pageable = PageRequest.of(0, 10);
        Specification spec = mock(Specification.class);
        Page<Bill> page = new PageImpl<Bill>(billList,pageable,2);
        List<BillDTO> dtoList = page.stream().map(bill -> modelmapper.convert(bill, BillDTO.class)).collect(Collectors.toList());
        when(billController.getListResponseEntity(pageable, spec))
                .thenReturn(
                        ResponseEntity.status(HttpStatus.OK).headers(pageHeaders(page.getTotalElements(), page.getTotalPages()))
                        .body(dtoList)
                );

        ResponseEntity<BillDTO> response=billController.getListResponseEntity(pageable, spec);

        assertNotNull(response);

    }*/
}















