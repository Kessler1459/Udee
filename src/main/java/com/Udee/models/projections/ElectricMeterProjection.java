package com.Udee.models.projections;

import com.Udee.models.Model;

public interface ElectricMeterProjection {
    Integer getId();
    String getSerial();
    Model getModel();
    void setId(Integer id);
}
