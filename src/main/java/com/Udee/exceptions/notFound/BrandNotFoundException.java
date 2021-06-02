package com.Udee.exceptions.notFound;

public class BrandNotFoundException extends ResourceNotFoundException{
    public BrandNotFoundException() {
        super("Brand not found");
    }
}
