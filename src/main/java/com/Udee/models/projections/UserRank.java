package com.Udee.models.projections;

import com.Udee.models.UserType;

public interface UserRank {
    Integer getId();

    Integer getDni();

    String getEmail();

    String getLastName();

    String getName();

    UserType getUserType();

    Integer getSum();
}
