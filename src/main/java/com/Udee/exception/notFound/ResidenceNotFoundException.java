package com.Udee.exception.notFound;

public class ResidenceNotFoundException extends ResourceNotFoundException{
    public ResidenceNotFoundException() {
        super("Residence not found");
    }
}
