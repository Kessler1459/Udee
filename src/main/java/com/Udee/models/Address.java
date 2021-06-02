package com.Udee.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;

    @NotNull @Size(min = 2, max = 50)
    private String street;

    @NotNull
    private Integer num;

    @Column(name = "floor_unit")
    @Size(min = 1, max = 10)
    private String floorUnit;

    @Column(name = "postal_code")
    @NotNull @Size(min = 3, max = 30)
    private String postalCode;

    @OneToOne( optional = false, orphanRemoval = true)
    @JoinColumn(name = "residence_id", nullable = false, unique = true)
    @JsonBackReference("residence-address")
    @ToString.Exclude
    private Residence residence;



}
