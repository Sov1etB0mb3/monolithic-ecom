package com.calt.burox.service;

import com.calt.burox.domain.User;
import com.calt.burox.repository.UserRepository;
import com.calt.burox.repository.search.UserSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.calt.burox.domain.User}.
 */
@Service
@Transactional
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final UserSearchRepository userSearchRepository;

    public UserService(UserRepository userRepository, UserSearchRepository userSearchRepository) {
        this.userRepository = userRepository;
        this.userSearchRepository = userSearchRepository;
    }

    /**
     * Save a user.
     *
     * @param user the entity to save.
     * @return the persisted entity.
     */
    public Mono<User> save(User user) {
        LOG.debug("Request to save User : {}", user);
        return userRepository.save(user).flatMap(userSearchRepository::save);
    }

    /**
     * Update a user.
     *
     * @param user the entity to save.
     * @return the persisted entity.
     */
    public Mono<User> update(User user) {
        LOG.debug("Request to update User : {}", user);
        return userRepository.save(user).flatMap(userSearchRepository::save);
    }

    /**
     * Partially update a user.
     *
     * @param user the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<User> partialUpdate(User user) {
        LOG.debug("Request to partially update User : {}", user);

        return userRepository
            .findById(user.getId())
            .map(existingUser -> {
                if (user.getUsername() != null) {
                    existingUser.setUsername(user.getUsername());
                }
                if (user.getPassword() != null) {
                    existingUser.setPassword(user.getPassword());
                }
                if (user.getCreatedAt() != null) {
                    existingUser.setCreatedAt(user.getCreatedAt());
                }
                if (user.getUpdatedAt() != null) {
                    existingUser.setUpdatedAt(user.getUpdatedAt());
                }

                return existingUser;
            })
            .flatMap(userRepository::save)
            .flatMap(savedUser -> {
                userSearchRepository.save(savedUser);
                return Mono.just(savedUser);
            });
    }

    /**
     * Get all the users.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<User> findAll(Pageable pageable) {
        LOG.debug("Request to get all Users");
        return userRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of users available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return userRepository.count();
    }

    /**
     * Returns the number of users available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return userSearchRepository.count();
    }

    /**
     * Get one user by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<User> findOne(Long id) {
        LOG.debug("Request to get User : {}", id);
        return userRepository.findById(id);
    }

    /**
     * Delete the user by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete User : {}", id);
        return userRepository.deleteById(id).then(userSearchRepository.deleteById(id));
    }

    /**
     * Search for the user corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<User> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Users for query {}", query);
        return userSearchRepository.search(query, pageable);
    }
}
