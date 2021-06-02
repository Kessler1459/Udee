package com.Udee.exceptions.notFound;

public abstract class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
