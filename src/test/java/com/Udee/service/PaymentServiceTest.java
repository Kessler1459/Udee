package com.Udee.service;

import com.Udee.exception.notFound.PaymentNotFoundException;
import com.Udee.models.*;
import com.Udee.repository.PaymentRepository;
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
import org.springframework.security.core.parameters.P;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {
    @Mock
    PaymentRepository paymentRepository;
    @InjectMocks
    PaymentService paymentService;
    Payment pay;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pay = Payment.builder().id(1).amount(BigDecimal.ONE).date(LocalDate.now()).build();
    }

    @Test
    void testAddPaymentNotThrows() {
        Bill bill = Bill.builder().build();
        when(paymentRepository.save(any(Payment.class))).thenReturn(pay);

        Payment result = assertDoesNotThrow(() -> paymentService.addPayment(bill, pay));

        assertNotNull(result.getBill());
        assertEquals(bill, result.getBill());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testAddPaymentThrows() {
        Bill bill = Bill.builder().payment(new Payment()).build();
        when(paymentRepository.save(any(Payment.class))).thenReturn(pay);

        assertThrows(HttpClientErrorException.class, () -> paymentService.addPayment(bill, pay));

        verify(paymentRepository, times(0)).save(any(Payment.class));
    }

    @Test
    void testFindByIdFound() {
        when(paymentRepository.findById(anyInt())).thenReturn(Optional.of(pay));

        Payment result = assertDoesNotThrow(() -> paymentService.findById(1));

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(paymentRepository, times(1)).findById(anyInt());
    }

    @Test
    void testFindByIdNotFound() {
        when(paymentRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> paymentService.findById(1));

        verify(paymentRepository, times(1)).findById(anyInt());
    }

    @Test
    void testFindAll() {
        Specification<Payment> spec = mock(Specification.class);
        Pageable pageable = PageRequest.of(0, 1);
        when(paymentRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(pay), pageable, 1));
        
        Page<Payment> result = paymentService.findAll(spec, pageable);

        assertEquals(1, result.getTotalElements());
        verify(paymentRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }
}

