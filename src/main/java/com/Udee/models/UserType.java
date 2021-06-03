package com.Udee.models;


public enum UserType {
    CLIENT("CLIENT"),
    EMPLOYEE("EMPLOYEE");

    private final String type;

    UserType(String type) {
        this.type = type;
    }

    public String getType(){
        return type;
    }
}
