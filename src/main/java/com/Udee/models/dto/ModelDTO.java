package com.Udee.models.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class ModelDTO {
    private Integer id;

    private String name;

    private BrandDTO brand;
}
