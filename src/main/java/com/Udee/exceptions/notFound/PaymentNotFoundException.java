package com.Udee.exceptions.notFound;

public class PaymentNotFoundException extends ResourceNotFoundException{
    public PaymentNotFoundException() {
        super("Payment not found");
    }
}
