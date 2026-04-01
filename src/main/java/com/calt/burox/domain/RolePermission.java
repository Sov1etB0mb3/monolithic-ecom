package com.calt.burox.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A RolePermission.
 */
@Table("role_permission")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "rolepermission")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RolePermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "users", "permissions" }, allowSetters = true)
    private Role role;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "rolePermissions" }, allowSetters = true)
    private Permission permission;

    @Column("role_id")
    private Long roleId;

    @Column("permission_id")
    private Long permissionId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RolePermission id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Role getRole() {
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
        this.roleId = role != null ? role.getId() : null;
    }

    public RolePermission role(Role role) {
        this.setRole(role);
        return this;
    }

    public Permission getPermission() {
        return this.permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
        this.permissionId = permission != null ? permission.getId() : null;
    }

    public RolePermission permission(Permission permission) {
        this.setPermission(permission);
        return this;
    }

    public Long getRoleId() {
        return this.roleId;
    }

    public void setRoleId(Long role) {
        this.roleId = role;
    }

    public Long getPermissionId() {
        return this.permissionId;
    }

    public void setPermissionId(Long permission) {
        this.permissionId = permission;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RolePermission)) {
            return false;
        }
        return getId() != null && getId().equals(((RolePermission) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RolePermission{" +
            "id=" + getId() +
            "}";
    }
}
