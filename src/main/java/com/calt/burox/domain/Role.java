package com.calt.burox.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Role.
 */
@Table("role")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "role")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @Column("description")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user", "role" }, allowSetters = true)
    private Set<UserRole> users = new HashSet<>();

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "role", "permission" }, allowSetters = true)
    private Set<RolePermission> permissions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Role id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Role name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Role description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<UserRole> getUsers() {
        return this.users;
    }

    public void setUsers(Set<UserRole> userRoles) {
        if (this.users != null) {
            this.users.forEach(i -> i.setRole(null));
        }
        if (userRoles != null) {
            userRoles.forEach(i -> i.setRole(this));
        }
        this.users = userRoles;
    }

    public Role users(Set<UserRole> userRoles) {
        this.setUsers(userRoles);
        return this;
    }

    public Role addUsers(UserRole userRole) {
        this.users.add(userRole);
        userRole.setRole(this);
        return this;
    }

    public Role removeUsers(UserRole userRole) {
        this.users.remove(userRole);
        userRole.setRole(null);
        return this;
    }

    public Set<RolePermission> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(Set<RolePermission> rolePermissions) {
        if (this.permissions != null) {
            this.permissions.forEach(i -> i.setRole(null));
        }
        if (rolePermissions != null) {
            rolePermissions.forEach(i -> i.setRole(this));
        }
        this.permissions = rolePermissions;
    }

    public Role permissions(Set<RolePermission> rolePermissions) {
        this.setPermissions(rolePermissions);
        return this;
    }

    public Role addPermissions(RolePermission rolePermission) {
        this.permissions.add(rolePermission);
        rolePermission.setRole(this);
        return this;
    }

    public Role removePermissions(RolePermission rolePermission) {
        this.permissions.remove(rolePermission);
        rolePermission.setRole(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Role)) {
            return false;
        }
        return getId() != null && getId().equals(((Role) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Role{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
