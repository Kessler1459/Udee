package com.Udee.controller;

import com.Udee.PostResponse;
import com.Udee.models.Rate;
import com.Udee.models.dto.MessageDTO;
import com.Udee.service.RateService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class RateControllerTest {
    @Mock
    RateService rateService;
    @InjectMocks
    RateController rateController;
    List<Rate> rates;

    @BeforeEach
    void setUp() {
        openMocks(this);
        rates = List.of(Rate.builder().name("rate1").priceXKW(2.0f).id(1).build(),
                Rate.builder().id(2).name("rate2").priceXKW(3.0f).build());
    }

    @Test
    void testAddRate() {
        Rate r = Rate.builder().name("rate1").id(1).priceXKW(2.0f).build();

        when(rateService.addRate(r)).thenReturn(r);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        PostResponse pr = new PostResponse("http://localhost/api/back-office/rates/" + r.getId(), HttpStatus.CREATED.getReasonPhrase());

        ResponseEntity<PostResponse> result = rateController.addRate(r);

        Assertions.assertEquals(pr, result.getBody());
        Assertions.assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(rateService, times(1)).addRate(any());
    }

    @Test
    void testFindAllHttp200() {
        Specification spec = mock(Specification.class);
        Pageable pageable = PageRequest.of(0, 2);
        Page<Rate> page= new PageImpl<>(rates,pageable,2);
        when(rateService.findAll(spec,pageable)).thenReturn(page);

        ResponseEntity<List<Rate>> result = rateController.findAll(spec,pageable);
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertEquals(page.getContent(), result.getBody());
        assertEquals("2", result.getHeaders().get("X-totalElements").get(0));
        assertEquals("1", result.getHeaders().get("X-totalPages").get(0));
        verify(rateService,times(1)).findAll(spec,pageable);
    }

    @Test
    void testFindAllHttp204() {
        Specification spec = mock(Specification.class);
        Pageable pageable = PageRequest.of(0, 2);
        Page<Rate> page= new PageImpl<>(Collections.emptyList(),pageable,0);
        when(rateService.findAll(spec,pageable)).thenReturn(page);

        ResponseEntity<List<Rate>> result = rateController.findAll(spec,pageable);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        Assertions.assertEquals(page.getContent(), result.getBody());
        assertEquals("0", result.getHeaders().get("X-totalElements").get(0));
        assertEquals("0", result.getHeaders().get("X-totalPages").get(0));
        verify(rateService,times(1)).findAll(spec,pageable);
    }

    @Test
    void testFindById() {
        Rate r = Rate.builder().name("rate1").priceXKW(2.0f).build();
        when(rateService.findById(anyInt())).thenReturn(r);

        ResponseEntity<Rate> result = rateController.findById(1);

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertEquals(r, result.getBody());
        verify(rateService,times(1)).findById(1);
    }

    @Test
    void testUpdateRate() {
        Rate r = Rate.builder().id(1).name("rate1").priceXKW(2.0f).build();
        when(rateService.updateRate(any())).thenReturn(r);

        ResponseEntity<Rate> result = rateController.updateRate(r);

        Assertions.assertEquals(1, result.getBody().getId());
        Assertions.assertEquals(HttpStatus.ACCEPTED, result.getStatusCode());
        verify(rateService,times(1)).updateRate(r);
    }

    @Test
    void testDeleteRate() {
        ResponseEntity<MessageDTO> result = rateController.deleteRate(anyInt());
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
