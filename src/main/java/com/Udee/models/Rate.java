package com.Udee.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "rates")
public class Rate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull @Size(min = 1)
    private String name;

    @NotNull @Min(0)
    private Float priceXKW;

    @OneToMany(mappedBy = "rate")
    @ToString.Exclude
    @JsonIgnore
    private List<Residence> residences;

    @OneToMany(mappedBy = "rate")
    @ToString.Exclude
    @JsonIgnore
    private List<Bill> bills;

}
