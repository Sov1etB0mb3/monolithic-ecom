package com.calt.burox.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_user_role")
@Getter
@Setter
@NoArgsConstructor
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id")
    @NotNull
    private Role role;
}
