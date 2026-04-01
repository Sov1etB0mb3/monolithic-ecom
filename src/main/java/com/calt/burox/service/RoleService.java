package com.calt.burox.service;

import com.calt.burox.repository.RoleRepository;
import com.calt.burox.repository.search.RoleSearchRepository;
import com.calt.burox.service.dto.RoleDTO;
import com.calt.burox.service.mapper.RoleMapper;
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

    private final RoleMapper roleMapper;

    private final RoleSearchRepository roleSearchRepository;

    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper, RoleSearchRepository roleSearchRepository) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.roleSearchRepository = roleSearchRepository;
    }

    /**
     * Save a role.
     *
     * @param roleDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<RoleDTO> save(RoleDTO roleDTO) {
        LOG.debug("Request to save Role : {}", roleDTO);
        return roleRepository.save(roleMapper.toEntity(roleDTO)).flatMap(roleSearchRepository::save).map(roleMapper::toDto);
    }

    /**
     * Update a role.
     *
     * @param roleDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<RoleDTO> update(RoleDTO roleDTO) {
        LOG.debug("Request to update Role : {}", roleDTO);
        return roleRepository.save(roleMapper.toEntity(roleDTO)).flatMap(roleSearchRepository::save).map(roleMapper::toDto);
    }

    /**
     * Partially update a role.
     *
     * @param roleDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<RoleDTO> partialUpdate(RoleDTO roleDTO) {
        LOG.debug("Request to partially update Role : {}", roleDTO);

        return roleRepository
            .findById(roleDTO.getId())
            .map(existingRole -> {
                roleMapper.partialUpdate(existingRole, roleDTO);

                return existingRole;
            })
            .flatMap(roleRepository::save)
            .flatMap(savedRole -> {
                roleSearchRepository.save(savedRole);
                return Mono.just(savedRole);
            })
            .map(roleMapper::toDto);
    }

    /**
     * Get all the roles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<RoleDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Roles");
        return roleRepository.findAllBy(pageable).map(roleMapper::toDto);
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
    public Mono<RoleDTO> findOne(Long id) {
        LOG.debug("Request to get Role : {}", id);
        return roleRepository.findById(id).map(roleMapper::toDto);
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
    public Flux<RoleDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Roles for query {}", query);
        return roleSearchRepository.search(query, pageable).map(roleMapper::toDto);
    }
}
