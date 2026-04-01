package com.calt.burox.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.calt.burox.domain.UserRole} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserRoleDTO implements Serializable {

    private Long id;

    @NotNull
    private UserDTO user;

    @NotNull
    private RoleDTO role;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public RoleDTO getRole() {
        return role;
    }

    public void setRole(RoleDTO role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserRoleDTO)) {
            return false;
        }

        UserRoleDTO userRoleDTO = (UserRoleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, userRoleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserRoleDTO{" +
            "id=" + getId() +
            ", user=" + getUser() +
            ", role=" + getRole() +
            "}";
    }
}
