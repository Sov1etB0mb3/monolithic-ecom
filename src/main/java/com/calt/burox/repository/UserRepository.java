package com.calt.burox.repository;

import com.calt.burox.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the User entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long>, UserRepositoryInternal {
    Flux<User> findAllBy(Pageable pageable);

    @Override
    <S extends User> Mono<S> save(S entity);

    @Override
    Flux<User> findAll();

    @Override
    Mono<User> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface UserRepositoryInternal {
    <S extends User> Mono<S> save(S entity);

    Flux<User> findAllBy(Pageable pageable);

    Flux<User> findAll();

    Mono<User> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<User> findAllBy(Pageable pageable, Criteria criteria);
}
