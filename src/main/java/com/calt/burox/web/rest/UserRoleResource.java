package com.calt.burox.web.rest;

import com.calt.burox.domain.UserRole;
import com.calt.burox.repository.UserRoleRepository;
import com.calt.burox.service.UserRoleService;
import com.calt.burox.web.rest.errors.BadRequestAlertException;
import com.calt.burox.web.rest.errors.ElasticsearchExceptionMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.calt.burox.domain.UserRole}.
 */
@RestController
@RequestMapping("/api/user-roles")
public class UserRoleResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserRoleResource.class);

    private static final String ENTITY_NAME = "userRole";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserRoleService userRoleService;

    private final UserRoleRepository userRoleRepository;

    public UserRoleResource(UserRoleService userRoleService, UserRoleRepository userRoleRepository) {
        this.userRoleService = userRoleService;
        this.userRoleRepository = userRoleRepository;
    }

    /**
     * {@code POST  /user-roles} : Create a new userRole.
     *
     * @param userRole the userRole to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userRole, or with status {@code 400 (Bad Request)} if the userRole has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<UserRole>> createUserRole(@Valid @RequestBody UserRole userRole) throws URISyntaxException {
        LOG.debug("REST request to save UserRole : {}", userRole);
        if (userRole.getId() != null) {
            throw new BadRequestAlertException("A new userRole cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return userRoleService
            .save(userRole)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/user-roles/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /user-roles/:id} : Updates an existing userRole.
     *
     * @param id the id of the userRole to save.
     * @param userRole the userRole to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userRole,
     * or with status {@code 400 (Bad Request)} if the userRole is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userRole couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<UserRole>> updateUserRole(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserRole userRole
    ) throws URISyntaxException {
        LOG.debug("REST request to update UserRole : {}, {}", id, userRole);
        if (userRole.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userRole.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return userRoleRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return userRoleService
                    .update(userRole)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /user-roles/:id} : Partial updates given fields of an existing userRole, field will ignore if it is null
     *
     * @param id the id of the userRole to save.
     * @param userRole the userRole to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userRole,
     * or with status {@code 400 (Bad Request)} if the userRole is not valid,
     * or with status {@code 404 (Not Found)} if the userRole is not found,
     * or with status {@code 500 (Internal Server Error)} if the userRole couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<UserRole>> partialUpdateUserRole(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserRole userRole
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update UserRole partially : {}, {}", id, userRole);
        if (userRole.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userRole.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return userRoleRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<UserRole> result = userRoleService.partialUpdate(userRole);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /user-roles} : get all the userRoles.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userRoles in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<UserRole>> getAllUserRoles() {
        LOG.debug("REST request to get all UserRoles");
        return userRoleService.findAll().collectList();
    }

    /**
     * {@code GET  /user-roles} : get all the userRoles as a stream.
     * @return the {@link Flux} of userRoles.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<UserRole> getAllUserRolesAsStream() {
        LOG.debug("REST request to get all UserRoles as a stream");
        return userRoleService.findAll();
    }

    /**
     * {@code GET  /user-roles/:id} : get the "id" userRole.
     *
     * @param id the id of the userRole to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userRole, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserRole>> getUserRole(@PathVariable("id") Long id) {
        LOG.debug("REST request to get UserRole : {}", id);
        Mono<UserRole> userRole = userRoleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userRole);
    }

    /**
     * {@code DELETE  /user-roles/:id} : delete the "id" userRole.
     *
     * @param id the id of the userRole to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUserRole(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete UserRole : {}", id);
        return userRoleService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }

    /**
     * {@code SEARCH  /user-roles/_search?query=:query} : search for the userRole corresponding
     * to the query.
     *
     * @param query the query of the userRole search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<List<UserRole>> searchUserRoles(@RequestParam("query") String query) {
        LOG.debug("REST request to search UserRoles for query {}", query);
        try {
            return userRoleService.search(query).collectList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
