package com.Udee.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "models")
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "model")
    @JsonBackReference("meter-model")
    @ToString.Exclude
    private List<ElectricMeter> electricMeter;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;
}
