package com.calt.burox.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "tbl_role_permisison")
@NoArgsConstructor
@AllArgsConstructor
public class RolePermission {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "role_id")
    @NotNull
    private Role role;

    @ManyToOne
    @JoinColumn(name = "permission_id")
    @NotNull
    private Permission permission;

    public RolePermission(Role role, Permission permission) {
        this.role = role;
        this.permission = permission;
    }
}
