package com.Udee.services;

import com.Udee.exceptions.notFound.PaymentNotFoundException;
import com.Udee.models.Bill;
import com.Udee.models.Payment;
import com.Udee.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment addPayment(Bill b, Payment payment) {
        if (b.getPayment()!=null){
            throw new HttpClientErrorException(HttpStatus.CONFLICT,"This bill was paid before");
        }
        payment.setBill(b);
        return paymentRepository.save(payment);
    }

    public Payment findById(Integer id) {
        return paymentRepository.findById(id).orElseThrow(PaymentNotFoundException::new);
    }

    public Page<Payment> findAll(Specification<Payment> spec, Pageable pagination) {
        return paymentRepository.findAll(spec,pagination);
    }
}
