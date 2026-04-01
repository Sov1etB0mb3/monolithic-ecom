package com.calt.burox.service.mapper;

import static com.calt.burox.domain.RolePermissionAsserts.*;
import static com.calt.burox.domain.RolePermissionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RolePermissionMapperTest {

    private RolePermissionMapper rolePermissionMapper;

    @BeforeEach
    void setUp() {
        rolePermissionMapper = new RolePermissionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRolePermissionSample1();
        var actual = rolePermissionMapper.toEntity(rolePermissionMapper.toDto(expected));
        assertRolePermissionAllPropertiesEquals(expected, actual);
    }
}
