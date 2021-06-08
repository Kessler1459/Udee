package com.Udee.controller;

import com.Udee.PostResponse;
import com.Udee.models.*;
import com.Udee.models.dto.PaymentDTO;
import com.Udee.models.dto.UserDTO;
import com.Udee.service.BillService;
import com.Udee.service.PaymentService;
import com.Udee.utils.ListMapper;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

class PaymentControllerTest {
    @Mock
    PaymentService paymentService;

    ModelMapper modelMapper;
    @Mock
    BillService billService;

    PaymentController paymentController;
    List<Payment> payments;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        modelMapper=new ModelMapper();
        paymentController=new PaymentController(paymentService,modelMapper,billService);
        payments=List.of(Payment.builder().id(1).build(),Payment.builder().id(2).build());
    }

    @Test
    void testAddPaymentWithAccess() {
        Authentication authentication=mock(Authentication.class);
        User u=User.builder().id(1).build();
        Bill b=Bill.builder().id(2).user(u).build();
        Payment payment=Payment.builder().id(1).build();
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        when(billService.findById(anyInt())).thenReturn(b);
        when((authentication.getPrincipal()))
                .thenReturn(UserDTO.builder().id(1).build());
        when(paymentService.addPayment(any(), any())).thenReturn(payment);
        PostResponse pr=new PostResponse("http://localhost/api/back-office/payments/"+payment.getId(), HttpStatus.CREATED.getReasonPhrase());

        ResponseEntity<PostResponse> result = paymentController.addPayment(1,payment,authentication);
        Assertions.assertDoesNotThrow(()->AccessDeniedException.class);
        Assertions.assertEquals(pr, result.getBody());
    }

    @Test
    void testAddPaymentWithoutAccess() {
        Authentication authentication=mock(Authentication.class);
        User u=User.builder().id(1).build();
        Bill b=Bill.builder().id(2).user(u).build();
        Payment payment=Payment.builder().id(1).build();
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        when(billService.findById(anyInt())).thenReturn(b);
        when((authentication.getPrincipal()))
                .thenReturn(UserDTO.builder().id(2).build());
        when(paymentService.addPayment(any(), any())).thenReturn(payment);

        Assertions.assertThrows(AccessDeniedException.class,() ->paymentController.addPayment(1,payment,authentication));
    }

    @Test
    void testFindById() {
        Payment payment=Payment.builder().id(1).build();
        when(paymentService.findById(anyInt())).thenReturn(payment);
        ResponseEntity<PaymentDTO> r=ResponseEntity.ok(modelMapper.map(payment,PaymentDTO.class));

        ResponseEntity<PaymentDTO> result = paymentController.findById(1);

        Assertions.assertEquals(r, result);
        verify(paymentService,times(1)).findById(anyInt());
    }

    @Test
    void testFindAllHttp200() {
        Specification spec=mock(Specification.class);
        Pageable pageable= PageRequest.of(0,2);
        Page<Payment> p= new PageImpl<>(payments,pageable,2);
        when(paymentService.findAll(any(), any())).thenReturn(p);
        List<PaymentDTO> dtoList= ListMapper.listToDto(modelMapper,payments,PaymentDTO.class);

        ResponseEntity<List<PaymentDTO>> result = paymentController.findAll(spec,pageable);

        Assertions.assertEquals(dtoList, result.getBody());
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertEquals("2", result.getHeaders().get("X-totalElements").get(0));
        Assertions.assertEquals("1",result.getHeaders().get("X-totalPages").get(0));
    }

    @Test
    void testFindAllHttp204() {
        Specification spec=mock(Specification.class);
        Pageable pageable= PageRequest.of(0,2);
        Page<Payment> p= new PageImpl<>(Collections.emptyList(),pageable,0);
        when(paymentService.findAll(any(), any())).thenReturn(p);

        ResponseEntity<List<PaymentDTO>> result = paymentController.findAll(spec,pageable);

        Assertions.assertEquals(Collections.emptyList(), result.getBody());
        Assertions.assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        Assertions.assertEquals("0", result.getHeaders().get("X-totalElements").get(0));
        Assertions.assertEquals("0",result.getHeaders().get("X-totalPages").get(0));
    }
}

