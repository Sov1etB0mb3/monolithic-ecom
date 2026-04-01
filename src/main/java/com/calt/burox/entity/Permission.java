package com.calt.burox.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_permisson")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL, orphanRemoval = false)
    private Set<RolePermission> rolePermissions = new HashSet<>();

    public Permission(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
