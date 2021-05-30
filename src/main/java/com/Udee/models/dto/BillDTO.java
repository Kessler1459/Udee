package com.Udee.models.dto;

import com.Udee.models.Payment;
import com.Udee.models.Rate;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BillDTO {
    private Integer id;
    private UserDTO user;
    private ElectricMeterDTO electricMeter;
    private MeasureDTO initialMeasure;
    private MeasureDTO lastMeasure;
    private LocalDate date;
    private LocalDate expiration;
    private Integer usage;
    private Rate rate;
    private Payment payment;
    private Float total;
}
