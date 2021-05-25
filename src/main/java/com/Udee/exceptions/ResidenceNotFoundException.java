package com.Udee.exceptions;

public class ResidenceNotFoundException extends ResourceNotFoundException{
    public ResidenceNotFoundException() {
        super("Residence not found");
    }
}
