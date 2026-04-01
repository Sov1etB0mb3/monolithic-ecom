package com.calt.burox.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class RolePermissionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static RolePermission getRolePermissionSample1() {
        return new RolePermission().id(1L);
    }

    public static RolePermission getRolePermissionSample2() {
        return new RolePermission().id(2L);
    }

    public static RolePermission getRolePermissionRandomSampleGenerator() {
        return new RolePermission().id(longCount.incrementAndGet());
    }
}
