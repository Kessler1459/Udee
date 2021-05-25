package com.Udee.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Rate rate = (Rate) o;

        return id != null && id.equals(rate.id);
    }

    @Override
    public int hashCode() {
        return 1101571590;
    }
}
