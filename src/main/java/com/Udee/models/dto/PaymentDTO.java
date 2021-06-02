package com.Udee.models.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentDTO {
    private Integer id;

    private BillDTO bill;
    private BigDecimal amount;
    private LocalDate date;
}
