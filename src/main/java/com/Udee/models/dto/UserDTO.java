package com.Udee.models.dto;

import com.Udee.models.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer id;
    private String email;
    private Integer dni;
    private UserType userType;
    private String name;
    private String lastName;
}
