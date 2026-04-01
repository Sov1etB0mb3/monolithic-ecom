package com.calt.burox.service;

import com.calt.burox.repository.UserRepository;
import com.calt.burox.repository.search.UserSearchRepository;
import com.calt.burox.service.dto.UserDTO;
import com.calt.burox.service.mapper.UserMapper;
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

    private final UserMapper userMapper;

    private final UserSearchRepository userSearchRepository;

    public UserService(UserRepository userRepository, UserMapper userMapper, UserSearchRepository userSearchRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userSearchRepository = userSearchRepository;
    }

    /**
     * Save a user.
     *
     * @param userDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<UserDTO> save(UserDTO userDTO) {
        LOG.debug("Request to save User : {}", userDTO);
        return userRepository.save(userMapper.toEntity(userDTO)).flatMap(userSearchRepository::save).map(userMapper::toDto);
    }

    /**
     * Update a user.
     *
     * @param userDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<UserDTO> update(UserDTO userDTO) {
        LOG.debug("Request to update User : {}", userDTO);
        return userRepository.save(userMapper.toEntity(userDTO)).flatMap(userSearchRepository::save).map(userMapper::toDto);
    }

    /**
     * Partially update a user.
     *
     * @param userDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<UserDTO> partialUpdate(UserDTO userDTO) {
        LOG.debug("Request to partially update User : {}", userDTO);

        return userRepository
            .findById(userDTO.getId())
            .map(existingUser -> {
                userMapper.partialUpdate(existingUser, userDTO);

                return existingUser;
            })
            .flatMap(userRepository::save)
            .flatMap(savedUser -> {
                userSearchRepository.save(savedUser);
                return Mono.just(savedUser);
            })
            .map(userMapper::toDto);
    }

    /**
     * Get all the users.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<UserDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Users");
        return userRepository.findAllBy(pageable).map(userMapper::toDto);
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
    public Mono<UserDTO> findOne(Long id) {
        LOG.debug("Request to get User : {}", id);
        return userRepository.findById(id).map(userMapper::toDto);
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
    public Flux<UserDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Users for query {}", query);
        return userSearchRepository.search(query, pageable).map(userMapper::toDto);
    }
}
