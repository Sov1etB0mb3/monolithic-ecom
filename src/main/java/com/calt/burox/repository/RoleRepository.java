package com.calt.burox.repository;

import com.calt.burox.domain.Role;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Role entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RoleRepository extends ReactiveCrudRepository<Role, Long>, RoleRepositoryInternal {
    Flux<Role> findAllBy(Pageable pageable);

    @Override
    <S extends Role> Mono<S> save(S entity);

    @Override
    Flux<Role> findAll();

    @Override
    Mono<Role> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface RoleRepositoryInternal {
    <S extends Role> Mono<S> save(S entity);

    Flux<Role> findAllBy(Pageable pageable);

    Flux<Role> findAll();

    Mono<Role> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Role> findAllBy(Pageable pageable, Criteria criteria);
}
