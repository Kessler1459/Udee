package com.Udee.exception.notFound;

public class RateNotFoundException extends ResourceNotFoundException {
    public RateNotFoundException() {
        super("Price per KW not found");
    }
}
