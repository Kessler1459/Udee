package com.Udee.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table(name = "measures")
public class Measure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "electric_meter_id")
    @ToString.Exclude
    @JsonBackReference
    @NotNull
    private ElectricMeter electricMeter;

    @Column(name = "`datetime`")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @NotNull
    private LocalDateTime dateTime;

    private Integer measure;

    @Null
    private Float price;

    @Column(name = "`usage`")
    @Null
    private Integer usage;

}
