package com.calt.burox.entity;

import jakarta.persistence.Id;
//import org.springframework.data.redis.core.RedisHash;
//import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE)
//@RedisHash("tbl_invalid_token")
public class InvalidToken {

    @Id
    private String id;

    //@TimeToLive(unit = TimeUnit.DAYS)
    private Long expirytime;
}
