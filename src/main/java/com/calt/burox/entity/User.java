package com.calt.burox.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> roles = new HashSet<>();

    @Column(name = "created_at", updatable = false)
    private Instant created_at;

    @Column(name = "updated_at")
    @LastModifiedDate
    private Instant updated_at;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, Instant created_at, Instant updated_at) {
        this.username = username;
        this.password = password;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }
}
