package com.calt.burox.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A UserRole.
 */
@Table("user_role")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "userrole")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "roles" }, allowSetters = true)
    private User user;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "users", "permissions" }, allowSetters = true)
    private Role role;

    @Column("user_id")
    private Long userId;

    @Column("role_id")
    private Long roleId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserRole id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public UserRole user(User user) {
        this.setUser(user);
        return this;
    }

    public Role getRole() {
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
        this.roleId = role != null ? role.getId() : null;
    }

    public UserRole role(Role role) {
        this.setRole(role);
        return this;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long user) {
        this.userId = user;
    }

    public Long getRoleId() {
        return this.roleId;
    }

    public void setRoleId(Long role) {
        this.roleId = role;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserRole)) {
            return false;
        }
        return getId() != null && getId().equals(((UserRole) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserRole{" +
            "id=" + getId() +
            "}";
    }
}
