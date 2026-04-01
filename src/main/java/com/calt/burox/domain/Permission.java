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
 * A Permission.
 */
@Table("permission")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "permission")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Permission implements Serializable {

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
    @JsonIgnoreProperties(value = { "role", "permission" }, allowSetters = true)
    private Set<RolePermission> rolePermissions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Permission id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Permission name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Permission description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<RolePermission> getRolePermissions() {
        return this.rolePermissions;
    }

    public void setRolePermissions(Set<RolePermission> rolePermissions) {
        if (this.rolePermissions != null) {
            this.rolePermissions.forEach(i -> i.setPermission(null));
        }
        if (rolePermissions != null) {
            rolePermissions.forEach(i -> i.setPermission(this));
        }
        this.rolePermissions = rolePermissions;
    }

    public Permission rolePermissions(Set<RolePermission> rolePermissions) {
        this.setRolePermissions(rolePermissions);
        return this;
    }

    public Permission addRolePermission(RolePermission rolePermission) {
        this.rolePermissions.add(rolePermission);
        rolePermission.setPermission(this);
        return this;
    }

    public Permission removeRolePermission(RolePermission rolePermission) {
        this.rolePermissions.remove(rolePermission);
        rolePermission.setPermission(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Permission)) {
            return false;
        }
        return getId() != null && getId().equals(((Permission) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Permission{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
