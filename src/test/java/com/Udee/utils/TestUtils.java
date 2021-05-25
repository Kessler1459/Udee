package com.Udee.utils;

import com.Udee.models.User;
import com.Udee.models.UserType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;

public class TestUtils {
    public static String aUserJSON(){
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class,new LocalDateSerializer())
                .registerTypeAdapter(LocalDate.class,new LocalDateDeserializer())
                .setPrettyPrinting().create();
        return gson.toJson(aUser());

    }

    public static User aUser(){
        User user=new User();
        user.setDni(34564745);
        user.setEmail("asdasdsfd@gmail.com");
        user.setPass("sdfdfg34fgsr");
        user.setUserType(UserType.CLIENT);
        user.setName("pepe");
        user.setLastName("afsdfsg");
        return user;
    }
}
