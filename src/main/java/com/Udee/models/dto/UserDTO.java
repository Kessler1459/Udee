package com.Udee.models.dto;

import com.Udee.models.UserType;
import lombok.Data;


@Data
public class UserDTO {
    private Integer id;
    private String email;
    private Integer dni;
    private UserType userType;
    private String name;
    private String lastName;
}
