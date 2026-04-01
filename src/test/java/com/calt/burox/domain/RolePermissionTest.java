package com.calt.burox.domain;

import static com.calt.burox.domain.PermissionTestSamples.*;
import static com.calt.burox.domain.RolePermissionTestSamples.*;
import static com.calt.burox.domain.RoleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.calt.burox.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RolePermissionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RolePermission.class);
        RolePermission rolePermission1 = getRolePermissionSample1();
        RolePermission rolePermission2 = new RolePermission();
        assertThat(rolePermission1).isNotEqualTo(rolePermission2);

        rolePermission2.setId(rolePermission1.getId());
        assertThat(rolePermission1).isEqualTo(rolePermission2);

        rolePermission2 = getRolePermissionSample2();
        assertThat(rolePermission1).isNotEqualTo(rolePermission2);
    }

    @Test
    void roleTest() {
        RolePermission rolePermission = getRolePermissionRandomSampleGenerator();
        Role roleBack = getRoleRandomSampleGenerator();

        rolePermission.setRole(roleBack);
        assertThat(rolePermission.getRole()).isEqualTo(roleBack);

        rolePermission.role(null);
        assertThat(rolePermission.getRole()).isNull();
    }

    @Test
    void permissionTest() {
        RolePermission rolePermission = getRolePermissionRandomSampleGenerator();
        Permission permissionBack = getPermissionRandomSampleGenerator();

        rolePermission.setPermission(permissionBack);
        assertThat(rolePermission.getPermission()).isEqualTo(permissionBack);

        rolePermission.permission(null);
        assertThat(rolePermission.getPermission()).isNull();
    }
}
