package com.Udee.services;

import com.Udee.exceptions.notFound.PaymentNotFoundException;
import com.Udee.models.Bill;
import com.Udee.models.Payment;
import com.Udee.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BillService billService;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, BillService billService) {
        this.paymentRepository = paymentRepository;
        this.billService = billService;
    }

    public Payment addPayment(Integer billId, Payment payment) {
        final Bill b=billService.findById(billId);
        if (b.getPayment()!=null){
            throw new HttpClientErrorException(HttpStatus.CONFLICT,"This bill was paid before");
        }
        payment.setBill(b);
        return paymentRepository.save(payment);
    }

    public Payment findById(Integer id) {
        return paymentRepository.findById(id).orElseThrow(PaymentNotFoundException::new);
    }
}
