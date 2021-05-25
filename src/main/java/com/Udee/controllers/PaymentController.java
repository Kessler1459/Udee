package com.Udee.controllers;

import com.Udee.PostResponse;
import com.Udee.models.Payment;
import com.Udee.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static com.Udee.utils.CheckPages.checkPages;
import static com.Udee.utils.PageHeaders.pageHeaders;
import static com.Udee.utils.EntityUrlBuilder.buildURL;

@RestController
@RequestMapping
public class PaymentController {
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/bills/{billId}/payments")
    public ResponseEntity<PostResponse> addPayment(@PathVariable Integer billId, @RequestBody Payment payment){
        payment=paymentService.addPayment(billId,payment);
        PostResponse res=new PostResponse(buildURL("payments",payment.getId().toString()), HttpStatus.CREATED.getReasonPhrase());
        return ResponseEntity.created(URI.create(res.getUrl())).body(res);
    }

    @GetMapping("/payments/{id}")
    public ResponseEntity<Payment> findById(@PathVariable Integer id){
        return ResponseEntity.ok(paymentService.findById(id));
    }


}
