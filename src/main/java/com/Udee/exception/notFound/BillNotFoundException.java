package com.Udee.exception.notFound;

public class BillNotFoundException extends ResourceNotFoundException{
    public BillNotFoundException() {
        super("Bill not found");
    }
}
