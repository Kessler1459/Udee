package com.Udee.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDate;


@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "electric_meter_id")
    private ElectricMeter electricMeter;

    @OneToOne
    @JoinColumn(name = "initial_measure_id")
    private Measure initialMeasure;

    @OneToOne
    @JoinColumn(name = "final_measure_id")

    private Measure lastMeasure;

    private LocalDate date;

    private LocalDate expiration;

    private Integer usage;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rate_id")
    private Rate rate;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "bill")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ToString.Exclude
    private Payment payment;

    private Float total;

}