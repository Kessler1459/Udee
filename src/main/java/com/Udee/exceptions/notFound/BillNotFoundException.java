package com.Udee.exceptions.notFound;

public class BillNotFoundException extends ResourceNotFoundException{
    public BillNotFoundException() {
        super("Bill not found");
    }
}
