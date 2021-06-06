package com.Udee.models.projections;

import com.Udee.models.Measure;
import com.Udee.models.Rate;

import java.time.LocalDate;

public interface BillProjection {
    Integer getId();

    UserProjection getUser();

    ElectricMeterProjection getElectricMeter();

    Measure getInitialMeasure();

    Measure getLastMeasure();

    Integer getUsage();

    Rate getRate();

    Float getTotal();

    LocalDate getDate();

    LocalDate getExpiration();

    void setId(Integer id);
}
