package com.Udee.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;


@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "brands")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(min = 2)
    private String name;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "brand")
    @JsonBackReference("meter-brand")
    @ToString.Exclude
    private List<ElectricMeter> electricMeter;

    public Brand(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Brand brand = (Brand) o;

        return id != null && id.equals(brand.id);
    }

    @Override
    public int hashCode() {
        return 1183461506;
    }
}
