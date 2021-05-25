package com.Udee.models.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ElectricMeterDTO {
    Integer id;
    String serial;
    BrandDTO brand;
    String model;
}
