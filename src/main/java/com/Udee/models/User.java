package com.Udee.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    @Email
    private String email;

    @NotNull
    @Min(10000000)
    private Integer dni;
    //todo talvez chequear el unique con dni+usertype
    @Column(name = "user_type_enum")
    @Enumerated(value = EnumType.STRING)
    private UserType userType;

    @NotNull
    @Size(min = 1)
    private String name;

    @Column(name = "last_name")
    private String lastName;

    @NotNull
    @Size(min = 7)
    private String pass;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Residence> residences;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Bill> bills;

}
