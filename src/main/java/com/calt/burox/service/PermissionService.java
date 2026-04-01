package com.calt.burox.service;

import com.calt.burox.repository.PermissionRepository;
import com.calt.burox.repository.search.PermissionSearchRepository;
import com.calt.burox.service.dto.PermissionDTO;
import com.calt.burox.service.mapper.PermissionMapper;
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

    private final PermissionMapper permissionMapper;

    private final PermissionSearchRepository permissionSearchRepository;

    public PermissionService(
        PermissionRepository permissionRepository,
        PermissionMapper permissionMapper,
        PermissionSearchRepository permissionSearchRepository
    ) {
        this.permissionRepository = permissionRepository;
        this.permissionMapper = permissionMapper;
        this.permissionSearchRepository = permissionSearchRepository;
    }

    /**
     * Save a permission.
     *
     * @param permissionDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<PermissionDTO> save(PermissionDTO permissionDTO) {
        LOG.debug("Request to save Permission : {}", permissionDTO);
        return permissionRepository
            .save(permissionMapper.toEntity(permissionDTO))
            .flatMap(permissionSearchRepository::save)
            .map(permissionMapper::toDto);
    }

    /**
     * Update a permission.
     *
     * @param permissionDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<PermissionDTO> update(PermissionDTO permissionDTO) {
        LOG.debug("Request to update Permission : {}", permissionDTO);
        return permissionRepository
            .save(permissionMapper.toEntity(permissionDTO))
            .flatMap(permissionSearchRepository::save)
            .map(permissionMapper::toDto);
    }

    /**
     * Partially update a permission.
     *
     * @param permissionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<PermissionDTO> partialUpdate(PermissionDTO permissionDTO) {
        LOG.debug("Request to partially update Permission : {}", permissionDTO);

        return permissionRepository
            .findById(permissionDTO.getId())
            .map(existingPermission -> {
                permissionMapper.partialUpdate(existingPermission, permissionDTO);

                return existingPermission;
            })
            .flatMap(permissionRepository::save)
            .flatMap(savedPermission -> {
                permissionSearchRepository.save(savedPermission);
                return Mono.just(savedPermission);
            })
            .map(permissionMapper::toDto);
    }

    /**
     * Get all the permissions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<PermissionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Permissions");
        return permissionRepository.findAllBy(pageable).map(permissionMapper::toDto);
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
    public Mono<PermissionDTO> findOne(Long id) {
        LOG.debug("Request to get Permission : {}", id);
        return permissionRepository.findById(id).map(permissionMapper::toDto);
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
    public Flux<PermissionDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Permissions for query {}", query);
        return permissionSearchRepository.search(query, pageable).map(permissionMapper::toDto);
    }
}
