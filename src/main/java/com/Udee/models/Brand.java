package com.Udee.models;


import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
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

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Model> models;

    public Brand(String name) {
        this.name = name;
    }

    public void addModel(Model m){
        models.add(m);
    }

    public Brand(Integer id, String name) {
        this.id = id;
        this.name = name;
        this.models=new ArrayList<>();
    }
}
