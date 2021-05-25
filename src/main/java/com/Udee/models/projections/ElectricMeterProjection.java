package com.Udee.models.projections;

import com.Udee.models.Brand;

public interface ElectricMeterProjection {
    Integer getId();
    String getSerial();
    Brand getBrand();
    String getModel();
}
