package com.calt.burox.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UserTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static User getUserSample1() {
        return new User().id(1L).username("username1").password("password1");
    }

    public static User getUserSample2() {
        return new User().id(2L).username("username2").password("password2");
    }

    public static User getUserRandomSampleGenerator() {
        return new User().id(longCount.incrementAndGet()).username(UUID.randomUUID().toString()).password(UUID.randomUUID().toString());
    }
}
