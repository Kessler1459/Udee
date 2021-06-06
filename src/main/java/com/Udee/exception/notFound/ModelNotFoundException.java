package com.Udee.exception.notFound;

public class ModelNotFoundException extends ResourceNotFoundException{
    public ModelNotFoundException() {
        super("Model not found");
    }
}
