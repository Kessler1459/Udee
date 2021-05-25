package com.Udee.exceptions;

public class BillNotFoundException extends ResourceNotFoundException{
    public BillNotFoundException() {
        super("Bill not found");
    }
}
