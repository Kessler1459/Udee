package com.Udee.models.projections;

import com.Udee.models.UserType;



public interface UserProjection {
    Integer getId();
    String getEmail();
    Integer getDni();
    UserType getUserType();
    String getName();
    String getLastName();
}
