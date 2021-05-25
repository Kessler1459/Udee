package com.Udee.models.dto;

import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class MeasureDTO {
    private BigInteger id;
    private LocalDateTime dateTime;
    private Integer usage;

}
