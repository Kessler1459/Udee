package com.Udee.models.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MeasureRDTO {
    String serialNumber;
    float value;
    String date;
    String password;
}
