package com.calt.burox.repository;

import com.calt.burox.domain.RolePermission;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the RolePermission entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RolePermissionRepository extends ReactiveCrudRepository<RolePermission, Long>, RolePermissionRepositoryInternal {
    @Query("SELECT * FROM role_permission entity WHERE entity.role_id = :id")
    Flux<RolePermission> findByRole(Long id);

    @Query("SELECT * FROM role_permission entity WHERE entity.role_id IS NULL")
    Flux<RolePermission> findAllWhereRoleIsNull();

    @Query("SELECT * FROM role_permission entity WHERE entity.permission_id = :id")
    Flux<RolePermission> findByPermission(Long id);

    @Query("SELECT * FROM role_permission entity WHERE entity.permission_id IS NULL")
    Flux<RolePermission> findAllWherePermissionIsNull();

    @Override
    <S extends RolePermission> Mono<S> save(S entity);

    @Override
    Flux<RolePermission> findAll();

    @Override
    Mono<RolePermission> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface RolePermissionRepositoryInternal {
    <S extends RolePermission> Mono<S> save(S entity);

    Flux<RolePermission> findAllBy(Pageable pageable);

    Flux<RolePermission> findAll();

    Mono<RolePermission> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<RolePermission> findAllBy(Pageable pageable, Criteria criteria);
}
