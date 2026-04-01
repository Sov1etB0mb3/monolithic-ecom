package com.calt.burox.web.rest;

import static com.calt.burox.domain.RolePermissionAsserts.*;
import static com.calt.burox.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.calt.burox.IntegrationTest;
import com.calt.burox.domain.Permission;
import com.calt.burox.domain.Role;
import com.calt.burox.domain.RolePermission;
import com.calt.burox.repository.EntityManager;
import com.calt.burox.repository.RolePermissionRepository;
import com.calt.burox.repository.search.RolePermissionSearchRepository;
import com.calt.burox.service.dto.RolePermissionDTO;
import com.calt.burox.service.mapper.RolePermissionMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link RolePermissionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RolePermissionResourceIT {

    private static final String ENTITY_API_URL = "/api/role-permissions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/role-permissions/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private RolePermissionSearchRepository rolePermissionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private RolePermission rolePermission;

    private RolePermission insertedRolePermission;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RolePermission createEntity(EntityManager em) {
        RolePermission rolePermission = new RolePermission();
        // Add required entity
        Role role;
        role = em.insert(RoleResourceIT.createEntity()).block();
        rolePermission.setRole(role);
        // Add required entity
        Permission permission;
        permission = em.insert(PermissionResourceIT.createEntity()).block();
        rolePermission.setPermission(permission);
        return rolePermission;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RolePermission createUpdatedEntity(EntityManager em) {
        RolePermission updatedRolePermission = new RolePermission();
        // Add required entity
        Role role;
        role = em.insert(RoleResourceIT.createUpdatedEntity()).block();
        updatedRolePermission.setRole(role);
        // Add required entity
        Permission permission;
        permission = em.insert(PermissionResourceIT.createUpdatedEntity()).block();
        updatedRolePermission.setPermission(permission);
        return updatedRolePermission;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(RolePermission.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        RoleResourceIT.deleteEntities(em);
        PermissionResourceIT.deleteEntities(em);
    }

    @BeforeEach
    void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    void initTest() {
        rolePermission = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedRolePermission != null) {
            rolePermissionRepository.delete(insertedRolePermission).block();
            rolePermissionSearchRepository.delete(insertedRolePermission).block();
            insertedRolePermission = null;
        }
        deleteEntities(em);
    }

    @Test
    void createRolePermission() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rolePermissionSearchRepository.findAll().collectList().block());
        // Create the RolePermission
        RolePermissionDTO rolePermissionDTO = rolePermissionMapper.toDto(rolePermission);
        var returnedRolePermissionDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rolePermissionDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(RolePermissionDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the RolePermission in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRolePermission = rolePermissionMapper.toEntity(returnedRolePermissionDTO);
        assertRolePermissionUpdatableFieldsEquals(returnedRolePermission, getPersistedRolePermission(returnedRolePermission));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(rolePermissionSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedRolePermission = returnedRolePermission;
    }

    @Test
    void createRolePermissionWithExistingId() throws Exception {
        // Create the RolePermission with an existing ID
        rolePermission.setId(1L);
        RolePermissionDTO rolePermissionDTO = rolePermissionMapper.toDto(rolePermission);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rolePermissionSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rolePermissionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RolePermission in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rolePermissionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllRolePermissionsAsStream() {
        // Initialize the database
        rolePermissionRepository.save(rolePermission).block();

        List<RolePermission> rolePermissionList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(RolePermissionDTO.class)
            .getResponseBody()
            .map(rolePermissionMapper::toEntity)
            .filter(rolePermission::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(rolePermissionList).isNotNull();
        assertThat(rolePermissionList).hasSize(1);
        RolePermission testRolePermission = rolePermissionList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertRolePermissionAllPropertiesEquals(rolePermission, testRolePermission);
        assertRolePermissionUpdatableFieldsEquals(rolePermission, testRolePermission);
    }

    @Test
    void getAllRolePermissions() {
        // Initialize the database
        insertedRolePermission = rolePermissionRepository.save(rolePermission).block();

        // Get all the rolePermissionList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(rolePermission.getId().intValue()));
    }

    @Test
    void getRolePermission() {
        // Initialize the database
        insertedRolePermission = rolePermissionRepository.save(rolePermission).block();

        // Get the rolePermission
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, rolePermission.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(rolePermission.getId().intValue()));
    }

    @Test
    void getNonExistingRolePermission() {
        // Get the rolePermission
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingRolePermission() throws Exception {
        // Initialize the database
        insertedRolePermission = rolePermissionRepository.save(rolePermission).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        rolePermissionSearchRepository.save(rolePermission).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rolePermissionSearchRepository.findAll().collectList().block());

        // Update the rolePermission
        RolePermission updatedRolePermission = rolePermissionRepository.findById(rolePermission.getId()).block();
        RolePermissionDTO rolePermissionDTO = rolePermissionMapper.toDto(updatedRolePermission);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rolePermissionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rolePermissionDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RolePermission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRolePermissionToMatchAllProperties(updatedRolePermission);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(rolePermissionSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<RolePermission> rolePermissionSearchList = Streamable.of(
                    rolePermissionSearchRepository.findAll().collectList().block()
                ).toList();
                RolePermission testRolePermissionSearch = rolePermissionSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertRolePermissionAllPropertiesEquals(testRolePermissionSearch, updatedRolePermission);
                assertRolePermissionUpdatableFieldsEquals(testRolePermissionSearch, updatedRolePermission);
            });
    }

    @Test
    void putNonExistingRolePermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rolePermissionSearchRepository.findAll().collectList().block());
        rolePermission.setId(longCount.incrementAndGet());

        // Create the RolePermission
        RolePermissionDTO rolePermissionDTO = rolePermissionMapper.toDto(rolePermission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rolePermissionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rolePermissionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RolePermission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rolePermissionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchRolePermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rolePermissionSearchRepository.findAll().collectList().block());
        rolePermission.setId(longCount.incrementAndGet());

        // Create the RolePermission
        RolePermissionDTO rolePermissionDTO = rolePermissionMapper.toDto(rolePermission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rolePermissionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RolePermission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rolePermissionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamRolePermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rolePermissionSearchRepository.findAll().collectList().block());
        rolePermission.setId(longCount.incrementAndGet());

        // Create the RolePermission
        RolePermissionDTO rolePermissionDTO = rolePermissionMapper.toDto(rolePermission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rolePermissionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RolePermission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rolePermissionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateRolePermissionWithPatch() throws Exception {
        // Initialize the database
        insertedRolePermission = rolePermissionRepository.save(rolePermission).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rolePermission using partial update
        RolePermission partialUpdatedRolePermission = new RolePermission();
        partialUpdatedRolePermission.setId(rolePermission.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRolePermission.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRolePermission))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RolePermission in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRolePermissionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRolePermission, rolePermission),
            getPersistedRolePermission(rolePermission)
        );
    }

    @Test
    void fullUpdateRolePermissionWithPatch() throws Exception {
        // Initialize the database
        insertedRolePermission = rolePermissionRepository.save(rolePermission).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rolePermission using partial update
        RolePermission partialUpdatedRolePermission = new RolePermission();
        partialUpdatedRolePermission.setId(rolePermission.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRolePermission.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRolePermission))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RolePermission in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRolePermissionUpdatableFieldsEquals(partialUpdatedRolePermission, getPersistedRolePermission(partialUpdatedRolePermission));
    }

    @Test
    void patchNonExistingRolePermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rolePermissionSearchRepository.findAll().collectList().block());
        rolePermission.setId(longCount.incrementAndGet());

        // Create the RolePermission
        RolePermissionDTO rolePermissionDTO = rolePermissionMapper.toDto(rolePermission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, rolePermissionDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(rolePermissionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RolePermission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rolePermissionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchRolePermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rolePermissionSearchRepository.findAll().collectList().block());
        rolePermission.setId(longCount.incrementAndGet());

        // Create the RolePermission
        RolePermissionDTO rolePermissionDTO = rolePermissionMapper.toDto(rolePermission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(rolePermissionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RolePermission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rolePermissionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamRolePermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rolePermissionSearchRepository.findAll().collectList().block());
        rolePermission.setId(longCount.incrementAndGet());

        // Create the RolePermission
        RolePermissionDTO rolePermissionDTO = rolePermissionMapper.toDto(rolePermission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(rolePermissionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RolePermission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rolePermissionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteRolePermission() {
        // Initialize the database
        insertedRolePermission = rolePermissionRepository.save(rolePermission).block();
        rolePermissionRepository.save(rolePermission).block();
        rolePermissionSearchRepository.save(rolePermission).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rolePermissionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the rolePermission
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, rolePermission.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rolePermissionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchRolePermission() {
        // Initialize the database
        insertedRolePermission = rolePermissionRepository.save(rolePermission).block();
        rolePermissionSearchRepository.save(rolePermission).block();

        // Search the rolePermission
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + rolePermission.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(rolePermission.getId().intValue()));
    }

    protected long getRepositoryCount() {
        return rolePermissionRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected RolePermission getPersistedRolePermission(RolePermission rolePermission) {
        return rolePermissionRepository.findById(rolePermission.getId()).block();
    }

    protected void assertPersistedRolePermissionToMatchAllProperties(RolePermission expectedRolePermission) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRolePermissionAllPropertiesEquals(expectedRolePermission, getPersistedRolePermission(expectedRolePermission));
        assertRolePermissionUpdatableFieldsEquals(expectedRolePermission, getPersistedRolePermission(expectedRolePermission));
    }

    protected void assertPersistedRolePermissionToMatchUpdatableProperties(RolePermission expectedRolePermission) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRolePermissionAllUpdatablePropertiesEquals(expectedRolePermission, getPersistedRolePermission(expectedRolePermission));
        assertRolePermissionUpdatableFieldsEquals(expectedRolePermission, getPersistedRolePermission(expectedRolePermission));
    }
}
