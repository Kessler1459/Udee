package com.Udee.models.projections;

import com.Udee.models.Measure;
import com.Udee.models.Rate;

public interface BillProjection {
    Integer getId();

    UserProjection getUser();

    ElectricMeterProjection getElectricMeter();

    Measure getInitialMeasure();

    Measure getLastMeasure();

    Integer getUsage();

    Rate getRate();

    Float getTotal();
}
