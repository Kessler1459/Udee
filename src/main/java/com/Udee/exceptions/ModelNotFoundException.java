package com.Udee.exceptions;

public class ModelNotFoundException extends ResourceNotFoundException{
    public ModelNotFoundException() {
        super("Model not found");
    }
}
