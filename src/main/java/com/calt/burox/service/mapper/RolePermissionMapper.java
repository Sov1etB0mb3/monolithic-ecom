package com.calt.burox.service.mapper;

import com.calt.burox.domain.Permission;
import com.calt.burox.domain.Role;
import com.calt.burox.domain.RolePermission;
import com.calt.burox.service.dto.PermissionDTO;
import com.calt.burox.service.dto.RoleDTO;
import com.calt.burox.service.dto.RolePermissionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RolePermission} and its DTO {@link RolePermissionDTO}.
 */
@Mapper(componentModel = "spring")
public interface RolePermissionMapper extends EntityMapper<RolePermissionDTO, RolePermission> {
    @Mapping(target = "role", source = "role", qualifiedByName = "roleId")
    @Mapping(target = "permission", source = "permission", qualifiedByName = "permissionId")
    RolePermissionDTO toDto(RolePermission s);

    @Named("roleId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RoleDTO toDtoRoleId(Role role);

    @Named("permissionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PermissionDTO toDtoPermissionId(Permission permission);
}
