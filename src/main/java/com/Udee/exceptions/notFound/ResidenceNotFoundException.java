package com.Udee.exceptions.notFound;

public class ResidenceNotFoundException extends ResourceNotFoundException{
    public ResidenceNotFoundException() {
        super("Residence not found");
    }
}
