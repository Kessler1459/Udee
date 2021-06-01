package com.Udee.models.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsageDTO {
    private Integer usage;
    private Float totalCost;

    public UsageDTO(Integer usage, Float totalCost) {
        this.usage = usage;
        this.totalCost = totalCost;
    }
}
