package com.Udee.exceptions;


public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException() {
        super("User not found");
    }
}
