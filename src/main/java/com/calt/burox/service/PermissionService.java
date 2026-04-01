package com.calt.burox.service;

import com.calt.burox.domain.Permission;
import com.calt.burox.repository.PermissionRepository;
import com.calt.burox.repository.search.PermissionSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.calt.burox.domain.Permission}.
 */
@Service
@Transactional
public class PermissionService {

    private static final Logger LOG = LoggerFactory.getLogger(PermissionService.class);

    private final PermissionRepository permissionRepository;

    private final PermissionSearchRepository permissionSearchRepository;

    public PermissionService(PermissionRepository permissionRepository, PermissionSearchRepository permissionSearchRepository) {
        this.permissionRepository = permissionRepository;
        this.permissionSearchRepository = permissionSearchRepository;
    }

    /**
     * Save a permission.
     *
     * @param permission the entity to save.
     * @return the persisted entity.
     */
    public Mono<Permission> save(Permission permission) {
        LOG.debug("Request to save Permission : {}", permission);
        return permissionRepository.save(permission).flatMap(permissionSearchRepository::save);
    }

    /**
     * Update a permission.
     *
     * @param permission the entity to save.
     * @return the persisted entity.
     */
    public Mono<Permission> update(Permission permission) {
        LOG.debug("Request to update Permission : {}", permission);
        return permissionRepository.save(permission).flatMap(permissionSearchRepository::save);
    }

    /**
     * Partially update a permission.
     *
     * @param permission the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Permission> partialUpdate(Permission permission) {
        LOG.debug("Request to partially update Permission : {}", permission);

        return permissionRepository
            .findById(permission.getId())
            .map(existingPermission -> {
                if (permission.getName() != null) {
                    existingPermission.setName(permission.getName());
                }
                if (permission.getDescription() != null) {
                    existingPermission.setDescription(permission.getDescription());
                }

                return existingPermission;
            })
            .flatMap(permissionRepository::save)
            .flatMap(savedPermission -> {
                permissionSearchRepository.save(savedPermission);
                return Mono.just(savedPermission);
            });
    }

    /**
     * Get all the permissions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Permission> findAll(Pageable pageable) {
        LOG.debug("Request to get all Permissions");
        return permissionRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of permissions available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return permissionRepository.count();
    }

    /**
     * Returns the number of permissions available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return permissionSearchRepository.count();
    }

    /**
     * Get one permission by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Permission> findOne(Long id) {
        LOG.debug("Request to get Permission : {}", id);
        return permissionRepository.findById(id);
    }

    /**
     * Delete the permission by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Permission : {}", id);
        return permissionRepository.deleteById(id).then(permissionSearchRepository.deleteById(id));
    }

    /**
     * Search for the permission corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Permission> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Permissions for query {}", query);
        return permissionSearchRepository.search(query, pageable);
    }
}
