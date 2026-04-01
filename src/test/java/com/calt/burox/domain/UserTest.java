package com.calt.burox.domain;

import static com.calt.burox.domain.UserRoleTestSamples.*;
import static com.calt.burox.domain.UserTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.calt.burox.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(User.class);
        User user1 = getUserSample1();
        User user2 = new User();
        assertThat(user1).isNotEqualTo(user2);

        user2.setId(user1.getId());
        assertThat(user1).isEqualTo(user2);

        user2 = getUserSample2();
        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    void rolesTest() {
        User user = getUserRandomSampleGenerator();
        UserRole userRoleBack = getUserRoleRandomSampleGenerator();

        user.addRoles(userRoleBack);
        assertThat(user.getRoles()).containsOnly(userRoleBack);
        assertThat(userRoleBack.getUser()).isEqualTo(user);

        user.removeRoles(userRoleBack);
        assertThat(user.getRoles()).doesNotContain(userRoleBack);
        assertThat(userRoleBack.getUser()).isNull();

        user.roles(new HashSet<>(Set.of(userRoleBack)));
        assertThat(user.getRoles()).containsOnly(userRoleBack);
        assertThat(userRoleBack.getUser()).isEqualTo(user);

        user.setRoles(new HashSet<>());
        assertThat(user.getRoles()).doesNotContain(userRoleBack);
        assertThat(userRoleBack.getUser()).isNull();
    }
}
