package com.calt.burox.service;

import com.calt.burox.domain.UserRole;
import com.calt.burox.repository.UserRoleRepository;
import com.calt.burox.repository.search.UserRoleSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.calt.burox.domain.UserRole}.
 */
@Service
@Transactional
public class UserRoleService {

    private static final Logger LOG = LoggerFactory.getLogger(UserRoleService.class);

    private final UserRoleRepository userRoleRepository;

    private final UserRoleSearchRepository userRoleSearchRepository;

    public UserRoleService(UserRoleRepository userRoleRepository, UserRoleSearchRepository userRoleSearchRepository) {
        this.userRoleRepository = userRoleRepository;
        this.userRoleSearchRepository = userRoleSearchRepository;
    }

    /**
     * Save a userRole.
     *
     * @param userRole the entity to save.
     * @return the persisted entity.
     */
    public Mono<UserRole> save(UserRole userRole) {
        LOG.debug("Request to save UserRole : {}", userRole);
        return userRoleRepository.save(userRole).flatMap(userRoleSearchRepository::save);
    }

    /**
     * Update a userRole.
     *
     * @param userRole the entity to save.
     * @return the persisted entity.
     */
    public Mono<UserRole> update(UserRole userRole) {
        LOG.debug("Request to update UserRole : {}", userRole);
        return userRoleRepository.save(userRole).flatMap(userRoleSearchRepository::save);
    }

    /**
     * Partially update a userRole.
     *
     * @param userRole the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<UserRole> partialUpdate(UserRole userRole) {
        LOG.debug("Request to partially update UserRole : {}", userRole);

        return userRoleRepository
            .findById(userRole.getId())
            .flatMap(userRoleRepository::save)
            .flatMap(savedUserRole -> {
                userRoleSearchRepository.save(savedUserRole);
                return Mono.just(savedUserRole);
            });
    }

    /**
     * Get all the userRoles.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<UserRole> findAll() {
        LOG.debug("Request to get all UserRoles");
        return userRoleRepository.findAll();
    }

    /**
     * Returns the number of userRoles available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return userRoleRepository.count();
    }

    /**
     * Returns the number of userRoles available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return userRoleSearchRepository.count();
    }

    /**
     * Get one userRole by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<UserRole> findOne(Long id) {
        LOG.debug("Request to get UserRole : {}", id);
        return userRoleRepository.findById(id);
    }

    /**
     * Delete the userRole by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete UserRole : {}", id);
        return userRoleRepository.deleteById(id).then(userRoleSearchRepository.deleteById(id));
    }

    /**
     * Search for the userRole corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<UserRole> search(String query) {
        LOG.debug("Request to search UserRoles for query {}", query);
        try {
            return userRoleSearchRepository.search(query);
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
