package com.Udee.exception.notFound;

public class MeasureNotFoundException extends ResourceNotFoundException{
    public MeasureNotFoundException() {
        super("Measure not found");
    }
}
