package com.calt.burox.service.mapper;

import static com.calt.burox.domain.PermissionAsserts.*;
import static com.calt.burox.domain.PermissionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PermissionMapperTest {

    private PermissionMapper permissionMapper;

    @BeforeEach
    void setUp() {
        permissionMapper = new PermissionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPermissionSample1();
        var actual = permissionMapper.toEntity(permissionMapper.toDto(expected));
        assertPermissionAllPropertiesEquals(expected, actual);
    }
}
