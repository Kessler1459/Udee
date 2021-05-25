package com.Udee.exceptions;

public class BrandNotFoundException extends ResourceNotFoundException{
    public BrandNotFoundException() {
        super("Brand not found");
    }
}
