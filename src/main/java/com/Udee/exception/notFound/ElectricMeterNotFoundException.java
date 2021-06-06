package com.Udee.exception.notFound;

public class ElectricMeterNotFoundException extends ResourceNotFoundException{
    public ElectricMeterNotFoundException() {
        super("Electric meter not found");
    }
}
