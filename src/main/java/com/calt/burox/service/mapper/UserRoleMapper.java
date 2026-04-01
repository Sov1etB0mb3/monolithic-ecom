package com.calt.burox.service.mapper;

import com.calt.burox.domain.Role;
import com.calt.burox.domain.User;
import com.calt.burox.domain.UserRole;
import com.calt.burox.service.dto.RoleDTO;
import com.calt.burox.service.dto.UserDTO;
import com.calt.burox.service.dto.UserRoleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserRole} and its DTO {@link UserRoleDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserRoleMapper extends EntityMapper<UserRoleDTO, UserRole> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    @Mapping(target = "role", source = "role", qualifiedByName = "roleId")
    UserRoleDTO toDto(UserRole s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("roleId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RoleDTO toDtoRoleId(Role role);
}
