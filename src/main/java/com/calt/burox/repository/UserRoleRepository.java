package com.calt.burox.repository;

import com.calt.burox.domain.UserRole;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the UserRole entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserRoleRepository extends ReactiveCrudRepository<UserRole, Long>, UserRoleRepositoryInternal {
    @Query("SELECT * FROM user_role entity WHERE entity.user_id = :id")
    Flux<UserRole> findByUser(Long id);

    @Query("SELECT * FROM user_role entity WHERE entity.user_id IS NULL")
    Flux<UserRole> findAllWhereUserIsNull();

    @Query("SELECT * FROM user_role entity WHERE entity.role_id = :id")
    Flux<UserRole> findByRole(Long id);

    @Query("SELECT * FROM user_role entity WHERE entity.role_id IS NULL")
    Flux<UserRole> findAllWhereRoleIsNull();

    @Override
    <S extends UserRole> Mono<S> save(S entity);

    @Override
    Flux<UserRole> findAll();

    @Override
    Mono<UserRole> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface UserRoleRepositoryInternal {
    <S extends UserRole> Mono<S> save(S entity);

    Flux<UserRole> findAllBy(Pageable pageable);

    Flux<UserRole> findAll();

    Mono<UserRole> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<UserRole> findAllBy(Pageable pageable, Criteria criteria);
}
