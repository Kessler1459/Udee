package com.Udee.exception.notFound;

public class PaymentNotFoundException extends ResourceNotFoundException{
    public PaymentNotFoundException() {
        super("Payment not found");
    }
}
