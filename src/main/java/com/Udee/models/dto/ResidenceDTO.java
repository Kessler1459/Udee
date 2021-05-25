package com.Udee.models.dto;

import com.Udee.models.Address;
import com.Udee.models.Rate;
import com.Udee.models.projections.ElectricMeterProjection;
import com.Udee.models.projections.UserProjection;
import lombok.Data;

@Data
public class ResidenceDTO {
    Integer id;
    Address address;
    ElectricMeterDTO electricMeter;
    Rate rate;
    UserDTO user;
}
