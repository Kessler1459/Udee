package com.Udee.exceptions.notFound;

public class ElectricMeterNotFoundException extends ResourceNotFoundException{
    public ElectricMeterNotFoundException() {
        super("Electric meter not found");
    }
}
