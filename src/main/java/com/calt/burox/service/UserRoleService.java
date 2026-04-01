package com.calt.burox.service;

import com.calt.burox.repository.UserRoleRepository;
import com.calt.burox.repository.search.UserRoleSearchRepository;
import com.calt.burox.service.dto.UserRoleDTO;
import com.calt.burox.service.mapper.UserRoleMapper;
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

    private final UserRoleMapper userRoleMapper;

    private final UserRoleSearchRepository userRoleSearchRepository;

    public UserRoleService(
        UserRoleRepository userRoleRepository,
        UserRoleMapper userRoleMapper,
        UserRoleSearchRepository userRoleSearchRepository
    ) {
        this.userRoleRepository = userRoleRepository;
        this.userRoleMapper = userRoleMapper;
        this.userRoleSearchRepository = userRoleSearchRepository;
    }

    /**
     * Save a userRole.
     *
     * @param userRoleDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<UserRoleDTO> save(UserRoleDTO userRoleDTO) {
        LOG.debug("Request to save UserRole : {}", userRoleDTO);
        return userRoleRepository
            .save(userRoleMapper.toEntity(userRoleDTO))
            .flatMap(userRoleSearchRepository::save)
            .map(userRoleMapper::toDto);
    }

    /**
     * Update a userRole.
     *
     * @param userRoleDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<UserRoleDTO> update(UserRoleDTO userRoleDTO) {
        LOG.debug("Request to update UserRole : {}", userRoleDTO);
        return userRoleRepository
            .save(userRoleMapper.toEntity(userRoleDTO))
            .flatMap(userRoleSearchRepository::save)
            .map(userRoleMapper::toDto);
    }

    /**
     * Partially update a userRole.
     *
     * @param userRoleDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<UserRoleDTO> partialUpdate(UserRoleDTO userRoleDTO) {
        LOG.debug("Request to partially update UserRole : {}", userRoleDTO);

        return userRoleRepository
            .findById(userRoleDTO.getId())
            .map(existingUserRole -> {
                userRoleMapper.partialUpdate(existingUserRole, userRoleDTO);

                return existingUserRole;
            })
            .flatMap(userRoleRepository::save)
            .flatMap(savedUserRole -> {
                userRoleSearchRepository.save(savedUserRole);
                return Mono.just(savedUserRole);
            })
            .map(userRoleMapper::toDto);
    }

    /**
     * Get all the userRoles.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<UserRoleDTO> findAll() {
        LOG.debug("Request to get all UserRoles");
        return userRoleRepository.findAll().map(userRoleMapper::toDto);
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
    public Mono<UserRoleDTO> findOne(Long id) {
        LOG.debug("Request to get UserRole : {}", id);
        return userRoleRepository.findById(id).map(userRoleMapper::toDto);
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
    public Flux<UserRoleDTO> search(String query) {
        LOG.debug("Request to search UserRoles for query {}", query);
        try {
            return userRoleSearchRepository.search(query).map(userRoleMapper::toDto);
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
