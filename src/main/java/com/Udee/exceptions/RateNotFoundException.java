package com.Udee.exceptions;

public class RateNotFoundException extends ResourceNotFoundException {
    public RateNotFoundException() {
        super("Price per KW not found");
    }
}
