package com.calt.burox.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.calt.burox.domain.RolePermission} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RolePermissionDTO implements Serializable {

    private Long id;

    @NotNull
    private RoleDTO role;

    @NotNull
    private PermissionDTO permission;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleDTO getRole() {
        return role;
    }

    public void setRole(RoleDTO role) {
        this.role = role;
    }

    public PermissionDTO getPermission() {
        return permission;
    }

    public void setPermission(PermissionDTO permission) {
        this.permission = permission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RolePermissionDTO)) {
            return false;
        }

        RolePermissionDTO rolePermissionDTO = (RolePermissionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, rolePermissionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RolePermissionDTO{" +
            "id=" + getId() +
            ", role=" + getRole() +
            ", permission=" + getPermission() +
            "}";
    }
}
