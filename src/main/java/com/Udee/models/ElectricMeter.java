package com.Udee.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;


@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "electric_meters")
//todo ver este serializable
public class ElectricMeter implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    @NotNull @Size(min = 3)
    String serial;

    @ManyToOne
    @JoinColumn(name = "model_id")
   // @JsonManagedReference("meter-brand")
    private Model model;


    @NotNull @Size(min = 9)
    private String pass;

    @OneToMany(mappedBy = "electricMeter")
    @ToString.Exclude
    private List<Measure> measures;

    @OneToOne(mappedBy = "electricMeter",fetch = FetchType.LAZY)
    //@JsonIgnore
    @ToString.Exclude
    private Residence residence;

    @OneToMany(mappedBy = "electricMeter", orphanRemoval = true)
    @ToString.Exclude
    private List<Bill> bills;

}
