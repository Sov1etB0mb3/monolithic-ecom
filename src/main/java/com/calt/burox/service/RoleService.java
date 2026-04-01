package com.calt.burox.service;

import com.calt.burox.domain.Role;
import com.calt.burox.repository.RoleRepository;
import com.calt.burox.repository.search.RoleSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.calt.burox.domain.Role}.
 */
@Service
@Transactional
public class RoleService {

    private static final Logger LOG = LoggerFactory.getLogger(RoleService.class);

    private final RoleRepository roleRepository;

    private final RoleSearchRepository roleSearchRepository;

    public RoleService(RoleRepository roleRepository, RoleSearchRepository roleSearchRepository) {
        this.roleRepository = roleRepository;
        this.roleSearchRepository = roleSearchRepository;
    }

    /**
     * Save a role.
     *
     * @param role the entity to save.
     * @return the persisted entity.
     */
    public Mono<Role> save(Role role) {
        LOG.debug("Request to save Role : {}", role);
        return roleRepository.save(role).flatMap(roleSearchRepository::save);
    }

    /**
     * Update a role.
     *
     * @param role the entity to save.
     * @return the persisted entity.
     */
    public Mono<Role> update(Role role) {
        LOG.debug("Request to update Role : {}", role);
        return roleRepository.save(role).flatMap(roleSearchRepository::save);
    }

    /**
     * Partially update a role.
     *
     * @param role the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Role> partialUpdate(Role role) {
        LOG.debug("Request to partially update Role : {}", role);

        return roleRepository
            .findById(role.getId())
            .map(existingRole -> {
                if (role.getName() != null) {
                    existingRole.setName(role.getName());
                }
                if (role.getDescription() != null) {
                    existingRole.setDescription(role.getDescription());
                }

                return existingRole;
            })
            .flatMap(roleRepository::save)
            .flatMap(savedRole -> {
                roleSearchRepository.save(savedRole);
                return Mono.just(savedRole);
            });
    }

    /**
     * Get all the roles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Role> findAll(Pageable pageable) {
        LOG.debug("Request to get all Roles");
        return roleRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of roles available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return roleRepository.count();
    }

    /**
     * Returns the number of roles available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return roleSearchRepository.count();
    }

    /**
     * Get one role by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Role> findOne(Long id) {
        LOG.debug("Request to get Role : {}", id);
        return roleRepository.findById(id);
    }

    /**
     * Delete the role by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Role : {}", id);
        return roleRepository.deleteById(id).then(roleSearchRepository.deleteById(id));
    }

    /**
     * Search for the role corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Role> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Roles for query {}", query);
        return roleSearchRepository.search(query, pageable);
    }
}
