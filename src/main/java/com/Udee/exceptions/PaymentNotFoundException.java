package com.Udee.exceptions;

public class PaymentNotFoundException extends ResourceNotFoundException{
    public PaymentNotFoundException() {
        super("Payment not found");
    }
}
