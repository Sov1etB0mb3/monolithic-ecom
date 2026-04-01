package com.calt.burox.domain;

import static com.calt.burox.domain.RolePermissionTestSamples.*;
import static com.calt.burox.domain.RoleTestSamples.*;
import static com.calt.burox.domain.UserRoleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.calt.burox.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RoleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Role.class);
        Role role1 = getRoleSample1();
        Role role2 = new Role();
        assertThat(role1).isNotEqualTo(role2);

        role2.setId(role1.getId());
        assertThat(role1).isEqualTo(role2);

        role2 = getRoleSample2();
        assertThat(role1).isNotEqualTo(role2);
    }

    @Test
    void usersTest() {
        Role role = getRoleRandomSampleGenerator();
        UserRole userRoleBack = getUserRoleRandomSampleGenerator();

        role.addUsers(userRoleBack);
        assertThat(role.getUsers()).containsOnly(userRoleBack);
        assertThat(userRoleBack.getRole()).isEqualTo(role);

        role.removeUsers(userRoleBack);
        assertThat(role.getUsers()).doesNotContain(userRoleBack);
        assertThat(userRoleBack.getRole()).isNull();

        role.users(new HashSet<>(Set.of(userRoleBack)));
        assertThat(role.getUsers()).containsOnly(userRoleBack);
        assertThat(userRoleBack.getRole()).isEqualTo(role);

        role.setUsers(new HashSet<>());
        assertThat(role.getUsers()).doesNotContain(userRoleBack);
        assertThat(userRoleBack.getRole()).isNull();
    }

    @Test
    void permissionsTest() {
        Role role = getRoleRandomSampleGenerator();
        RolePermission rolePermissionBack = getRolePermissionRandomSampleGenerator();

        role.addPermissions(rolePermissionBack);
        assertThat(role.getPermissions()).containsOnly(rolePermissionBack);
        assertThat(rolePermissionBack.getRole()).isEqualTo(role);

        role.removePermissions(rolePermissionBack);
        assertThat(role.getPermissions()).doesNotContain(rolePermissionBack);
        assertThat(rolePermissionBack.getRole()).isNull();

        role.permissions(new HashSet<>(Set.of(rolePermissionBack)));
        assertThat(role.getPermissions()).containsOnly(rolePermissionBack);
        assertThat(rolePermissionBack.getRole()).isEqualTo(role);

        role.setPermissions(new HashSet<>());
        assertThat(role.getPermissions()).doesNotContain(rolePermissionBack);
        assertThat(rolePermissionBack.getRole()).isNull();
    }
}
