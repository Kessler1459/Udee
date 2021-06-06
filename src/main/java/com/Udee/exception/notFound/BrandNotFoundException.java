package com.Udee.exception.notFound;

public class BrandNotFoundException extends ResourceNotFoundException{
    public BrandNotFoundException() {
        super("Brand not found");
    }
}
