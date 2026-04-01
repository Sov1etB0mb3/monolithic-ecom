package com.calt.burox.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.calt.burox.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RolePermissionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RolePermissionDTO.class);
        RolePermissionDTO rolePermissionDTO1 = new RolePermissionDTO();
        rolePermissionDTO1.setId(1L);
        RolePermissionDTO rolePermissionDTO2 = new RolePermissionDTO();
        assertThat(rolePermissionDTO1).isNotEqualTo(rolePermissionDTO2);
        rolePermissionDTO2.setId(rolePermissionDTO1.getId());
        assertThat(rolePermissionDTO1).isEqualTo(rolePermissionDTO2);
        rolePermissionDTO2.setId(2L);
        assertThat(rolePermissionDTO1).isNotEqualTo(rolePermissionDTO2);
        rolePermissionDTO1.setId(null);
        assertThat(rolePermissionDTO1).isNotEqualTo(rolePermissionDTO2);
    }
}
