package com.example.security_jwt.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "companies")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "users", callSuper = false)
@ToString(exclude = "users")
public class Company extends BaseEntity {

    @Column(name = "company_name")
    private String companyName;
    @Column(name = "address")
    private String address;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private Set<Equipment> equipmentSet= new HashSet<>();
}
