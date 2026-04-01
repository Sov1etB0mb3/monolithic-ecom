package com.calt.burox.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "tbl_refresh_token")
public class RefreshToken {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "userid")
    private Integer userid;

    @Column(name = "refreshtoken")
    private String refreshtoken;

    @Column(name = "expirytime")
    private Instant expirytime;

    @Column(name = "jkt")
    private String jkt;

    @Column(name = "valid")
    private boolean valid = true;
}
