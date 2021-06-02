package com.Udee.exceptions.notFound;

public class ModelNotFoundException extends ResourceNotFoundException{
    public ModelNotFoundException() {
        super("Model not found");
    }
}
