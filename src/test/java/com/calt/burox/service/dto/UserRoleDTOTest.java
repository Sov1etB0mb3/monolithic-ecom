package com.calt.burox.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.calt.burox.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserRoleDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserRoleDTO.class);
        UserRoleDTO userRoleDTO1 = new UserRoleDTO();
        userRoleDTO1.setId(1L);
        UserRoleDTO userRoleDTO2 = new UserRoleDTO();
        assertThat(userRoleDTO1).isNotEqualTo(userRoleDTO2);
        userRoleDTO2.setId(userRoleDTO1.getId());
        assertThat(userRoleDTO1).isEqualTo(userRoleDTO2);
        userRoleDTO2.setId(2L);
        assertThat(userRoleDTO1).isNotEqualTo(userRoleDTO2);
        userRoleDTO1.setId(null);
        assertThat(userRoleDTO1).isNotEqualTo(userRoleDTO2);
    }
}
