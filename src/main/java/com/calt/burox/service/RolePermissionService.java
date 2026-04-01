package com.calt.burox.service;

import com.calt.burox.repository.RolePermissionRepository;
import com.calt.burox.repository.search.RolePermissionSearchRepository;
import com.calt.burox.service.dto.RolePermissionDTO;
import com.calt.burox.service.mapper.RolePermissionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.calt.burox.domain.RolePermission}.
 */
@Service
@Transactional
public class RolePermissionService {

    private static final Logger LOG = LoggerFactory.getLogger(RolePermissionService.class);

    private final RolePermissionRepository rolePermissionRepository;

    private final RolePermissionMapper rolePermissionMapper;

    private final RolePermissionSearchRepository rolePermissionSearchRepository;

    public RolePermissionService(
        RolePermissionRepository rolePermissionRepository,
        RolePermissionMapper rolePermissionMapper,
        RolePermissionSearchRepository rolePermissionSearchRepository
    ) {
        this.rolePermissionRepository = rolePermissionRepository;
        this.rolePermissionMapper = rolePermissionMapper;
        this.rolePermissionSearchRepository = rolePermissionSearchRepository;
    }

    /**
     * Save a rolePermission.
     *
     * @param rolePermissionDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<RolePermissionDTO> save(RolePermissionDTO rolePermissionDTO) {
        LOG.debug("Request to save RolePermission : {}", rolePermissionDTO);
        return rolePermissionRepository
            .save(rolePermissionMapper.toEntity(rolePermissionDTO))
            .flatMap(rolePermissionSearchRepository::save)
            .map(rolePermissionMapper::toDto);
    }

    /**
     * Update a rolePermission.
     *
     * @param rolePermissionDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<RolePermissionDTO> update(RolePermissionDTO rolePermissionDTO) {
        LOG.debug("Request to update RolePermission : {}", rolePermissionDTO);
        return rolePermissionRepository
            .save(rolePermissionMapper.toEntity(rolePermissionDTO))
            .flatMap(rolePermissionSearchRepository::save)
            .map(rolePermissionMapper::toDto);
    }

    /**
     * Partially update a rolePermission.
     *
     * @param rolePermissionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<RolePermissionDTO> partialUpdate(RolePermissionDTO rolePermissionDTO) {
        LOG.debug("Request to partially update RolePermission : {}", rolePermissionDTO);

        return rolePermissionRepository
            .findById(rolePermissionDTO.getId())
            .map(existingRolePermission -> {
                rolePermissionMapper.partialUpdate(existingRolePermission, rolePermissionDTO);

                return existingRolePermission;
            })
            .flatMap(rolePermissionRepository::save)
            .flatMap(savedRolePermission -> {
                rolePermissionSearchRepository.save(savedRolePermission);
                return Mono.just(savedRolePermission);
            })
            .map(rolePermissionMapper::toDto);
    }

    /**
     * Get all the rolePermissions.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<RolePermissionDTO> findAll() {
        LOG.debug("Request to get all RolePermissions");
        return rolePermissionRepository.findAll().map(rolePermissionMapper::toDto);
    }

    /**
     * Returns the number of rolePermissions available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return rolePermissionRepository.count();
    }

    /**
     * Returns the number of rolePermissions available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return rolePermissionSearchRepository.count();
    }

    /**
     * Get one rolePermission by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<RolePermissionDTO> findOne(Long id) {
        LOG.debug("Request to get RolePermission : {}", id);
        return rolePermissionRepository.findById(id).map(rolePermissionMapper::toDto);
    }

    /**
     * Delete the rolePermission by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete RolePermission : {}", id);
        return rolePermissionRepository.deleteById(id).then(rolePermissionSearchRepository.deleteById(id));
    }

    /**
     * Search for the rolePermission corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<RolePermissionDTO> search(String query) {
        LOG.debug("Request to search RolePermissions for query {}", query);
        try {
            return rolePermissionSearchRepository.search(query).map(rolePermissionMapper::toDto);
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
