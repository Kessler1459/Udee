package com.Udee.models.projections;

import com.Udee.models.Address;
import com.Udee.models.Rate;

public interface ResidenceProjection {
    Integer getId();
    Address getAddress();
    ElectricMeterProjection getElectricMeter();
    Rate getRate();
    UserProjection getUser();
}
