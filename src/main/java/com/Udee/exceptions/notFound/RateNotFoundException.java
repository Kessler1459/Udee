package com.Udee.exceptions.notFound;

public class RateNotFoundException extends ResourceNotFoundException {
    public RateNotFoundException() {
        super("Price per KW not found");
    }
}
