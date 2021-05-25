package com.Udee.exceptions;

public class ElectricMeterNotFoundException extends ResourceNotFoundException{
    public ElectricMeterNotFoundException() {
        super("Electric meter not found");
    }
}
