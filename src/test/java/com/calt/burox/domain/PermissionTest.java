package com.calt.burox.domain;

import static com.calt.burox.domain.PermissionTestSamples.*;
import static com.calt.burox.domain.RolePermissionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.calt.burox.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PermissionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Permission.class);
        Permission permission1 = getPermissionSample1();
        Permission permission2 = new Permission();
        assertThat(permission1).isNotEqualTo(permission2);

        permission2.setId(permission1.getId());
        assertThat(permission1).isEqualTo(permission2);

        permission2 = getPermissionSample2();
        assertThat(permission1).isNotEqualTo(permission2);
    }

    @Test
    void rolePermissionTest() {
        Permission permission = getPermissionRandomSampleGenerator();
        RolePermission rolePermissionBack = getRolePermissionRandomSampleGenerator();

        permission.addRolePermission(rolePermissionBack);
        assertThat(permission.getRolePermissions()).containsOnly(rolePermissionBack);
        assertThat(rolePermissionBack.getPermission()).isEqualTo(permission);

        permission.removeRolePermission(rolePermissionBack);
        assertThat(permission.getRolePermissions()).doesNotContain(rolePermissionBack);
        assertThat(rolePermissionBack.getPermission()).isNull();

        permission.rolePermissions(new HashSet<>(Set.of(rolePermissionBack)));
        assertThat(permission.getRolePermissions()).containsOnly(rolePermissionBack);
        assertThat(rolePermissionBack.getPermission()).isEqualTo(permission);

        permission.setRolePermissions(new HashSet<>());
        assertThat(permission.getRolePermissions()).doesNotContain(rolePermissionBack);
        assertThat(rolePermissionBack.getPermission()).isNull();
    }
}
