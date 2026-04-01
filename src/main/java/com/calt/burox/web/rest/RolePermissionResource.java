package com.calt.burox.web.rest;

import com.calt.burox.domain.RolePermission;
import com.calt.burox.repository.RolePermissionRepository;
import com.calt.burox.service.RolePermissionService;
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
 * REST controller for managing {@link com.calt.burox.domain.RolePermission}.
 */
@RestController
@RequestMapping("/api/role-permissions")
public class RolePermissionResource {

    private static final Logger LOG = LoggerFactory.getLogger(RolePermissionResource.class);

    private static final String ENTITY_NAME = "rolePermission";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RolePermissionService rolePermissionService;

    private final RolePermissionRepository rolePermissionRepository;

    public RolePermissionResource(RolePermissionService rolePermissionService, RolePermissionRepository rolePermissionRepository) {
        this.rolePermissionService = rolePermissionService;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    /**
     * {@code POST  /role-permissions} : Create a new rolePermission.
     *
     * @param rolePermission the rolePermission to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rolePermission, or with status {@code 400 (Bad Request)} if the rolePermission has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<RolePermission>> createRolePermission(@Valid @RequestBody RolePermission rolePermission)
        throws URISyntaxException {
        LOG.debug("REST request to save RolePermission : {}", rolePermission);
        if (rolePermission.getId() != null) {
            throw new BadRequestAlertException("A new rolePermission cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return rolePermissionService
            .save(rolePermission)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/role-permissions/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /role-permissions/:id} : Updates an existing rolePermission.
     *
     * @param id the id of the rolePermission to save.
     * @param rolePermission the rolePermission to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rolePermission,
     * or with status {@code 400 (Bad Request)} if the rolePermission is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rolePermission couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<RolePermission>> updateRolePermission(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RolePermission rolePermission
    ) throws URISyntaxException {
        LOG.debug("REST request to update RolePermission : {}, {}", id, rolePermission);
        if (rolePermission.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rolePermission.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rolePermissionRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return rolePermissionService
                    .update(rolePermission)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /role-permissions/:id} : Partial updates given fields of an existing rolePermission, field will ignore if it is null
     *
     * @param id the id of the rolePermission to save.
     * @param rolePermission the rolePermission to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rolePermission,
     * or with status {@code 400 (Bad Request)} if the rolePermission is not valid,
     * or with status {@code 404 (Not Found)} if the rolePermission is not found,
     * or with status {@code 500 (Internal Server Error)} if the rolePermission couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<RolePermission>> partialUpdateRolePermission(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RolePermission rolePermission
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update RolePermission partially : {}, {}", id, rolePermission);
        if (rolePermission.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rolePermission.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rolePermissionRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<RolePermission> result = rolePermissionService.partialUpdate(rolePermission);

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
     * {@code GET  /role-permissions} : get all the rolePermissions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rolePermissions in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<RolePermission>> getAllRolePermissions() {
        LOG.debug("REST request to get all RolePermissions");
        return rolePermissionService.findAll().collectList();
    }

    /**
     * {@code GET  /role-permissions} : get all the rolePermissions as a stream.
     * @return the {@link Flux} of rolePermissions.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<RolePermission> getAllRolePermissionsAsStream() {
        LOG.debug("REST request to get all RolePermissions as a stream");
        return rolePermissionService.findAll();
    }

    /**
     * {@code GET  /role-permissions/:id} : get the "id" rolePermission.
     *
     * @param id the id of the rolePermission to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rolePermission, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<RolePermission>> getRolePermission(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RolePermission : {}", id);
        Mono<RolePermission> rolePermission = rolePermissionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rolePermission);
    }

    /**
     * {@code DELETE  /role-permissions/:id} : delete the "id" rolePermission.
     *
     * @param id the id of the rolePermission to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteRolePermission(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete RolePermission : {}", id);
        return rolePermissionService
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
     * {@code SEARCH  /role-permissions/_search?query=:query} : search for the rolePermission corresponding
     * to the query.
     *
     * @param query the query of the rolePermission search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<List<RolePermission>> searchRolePermissions(@RequestParam("query") String query) {
        LOG.debug("REST request to search RolePermissions for query {}", query);
        try {
            return rolePermissionService.search(query).collectList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
