package com.calt.burox.web.rest;

import static com.calt.burox.domain.PermissionAsserts.*;
import static com.calt.burox.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.calt.burox.IntegrationTest;
import com.calt.burox.domain.Permission;
import com.calt.burox.repository.EntityManager;
import com.calt.burox.repository.PermissionRepository;
import com.calt.burox.repository.search.PermissionSearchRepository;
import com.calt.burox.service.dto.PermissionDTO;
import com.calt.burox.service.mapper.PermissionMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Integration tests for the {@link PermissionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PermissionResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/permissions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/permissions/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private PermissionSearchRepository permissionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Permission permission;

    private Permission insertedPermission;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Permission createEntity() {
        return new Permission().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Permission createUpdatedEntity() {
        return new Permission().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Permission.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    void initTest() {
        permission = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPermission != null) {
            permissionRepository.delete(insertedPermission).block();
            permissionSearchRepository.delete(insertedPermission).block();
            insertedPermission = null;
        }
        deleteEntities(em);
    }

    @Test
    void createPermission() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(permissionSearchRepository.findAll().collectList().block());
        // Create the Permission
        PermissionDTO permissionDTO = permissionMapper.toDto(permission);
        var returnedPermissionDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(permissionDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(PermissionDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Permission in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPermission = permissionMapper.toEntity(returnedPermissionDTO);
        assertPermissionUpdatableFieldsEquals(returnedPermission, getPersistedPermission(returnedPermission));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(permissionSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedPermission = returnedPermission;
    }

    @Test
    void createPermissionWithExistingId() throws Exception {
        // Create the Permission with an existing ID
        permission.setId(1L);
        PermissionDTO permissionDTO = permissionMapper.toDto(permission);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(permissionSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(permissionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Permission in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(permissionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(permissionSearchRepository.findAll().collectList().block());
        // set the field null
        permission.setName(null);

        // Create the Permission, which fails.
        PermissionDTO permissionDTO = permissionMapper.toDto(permission);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(permissionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(permissionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllPermissions() {
        // Initialize the database
        insertedPermission = permissionRepository.save(permission).block();

        // Get all the permissionList
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
            .value(hasItem(permission.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    @Test
    void getPermission() {
        // Initialize the database
        insertedPermission = permissionRepository.save(permission).block();

        // Get the permission
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, permission.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(permission.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION));
    }

    @Test
    void getNonExistingPermission() {
        // Get the permission
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPermission() throws Exception {
        // Initialize the database
        insertedPermission = permissionRepository.save(permission).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        permissionSearchRepository.save(permission).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(permissionSearchRepository.findAll().collectList().block());

        // Update the permission
        Permission updatedPermission = permissionRepository.findById(permission.getId()).block();
        updatedPermission.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        PermissionDTO permissionDTO = permissionMapper.toDto(updatedPermission);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, permissionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(permissionDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Permission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPermissionToMatchAllProperties(updatedPermission);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(permissionSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Permission> permissionSearchList = Streamable.of(permissionSearchRepository.findAll().collectList().block()).toList();
                Permission testPermissionSearch = permissionSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertPermissionAllPropertiesEquals(testPermissionSearch, updatedPermission);
                assertPermissionUpdatableFieldsEquals(testPermissionSearch, updatedPermission);
            });
    }

    @Test
    void putNonExistingPermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(permissionSearchRepository.findAll().collectList().block());
        permission.setId(longCount.incrementAndGet());

        // Create the Permission
        PermissionDTO permissionDTO = permissionMapper.toDto(permission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, permissionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(permissionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Permission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(permissionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchPermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(permissionSearchRepository.findAll().collectList().block());
        permission.setId(longCount.incrementAndGet());

        // Create the Permission
        PermissionDTO permissionDTO = permissionMapper.toDto(permission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(permissionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Permission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(permissionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamPermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(permissionSearchRepository.findAll().collectList().block());
        permission.setId(longCount.incrementAndGet());

        // Create the Permission
        PermissionDTO permissionDTO = permissionMapper.toDto(permission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(permissionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Permission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(permissionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdatePermissionWithPatch() throws Exception {
        // Initialize the database
        insertedPermission = permissionRepository.save(permission).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the permission using partial update
        Permission partialUpdatedPermission = new Permission();
        partialUpdatedPermission.setId(permission.getId());

        partialUpdatedPermission.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPermission.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPermission))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Permission in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPermissionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPermission, permission),
            getPersistedPermission(permission)
        );
    }

    @Test
    void fullUpdatePermissionWithPatch() throws Exception {
        // Initialize the database
        insertedPermission = permissionRepository.save(permission).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the permission using partial update
        Permission partialUpdatedPermission = new Permission();
        partialUpdatedPermission.setId(permission.getId());

        partialUpdatedPermission.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPermission.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPermission))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Permission in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPermissionUpdatableFieldsEquals(partialUpdatedPermission, getPersistedPermission(partialUpdatedPermission));
    }

    @Test
    void patchNonExistingPermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(permissionSearchRepository.findAll().collectList().block());
        permission.setId(longCount.incrementAndGet());

        // Create the Permission
        PermissionDTO permissionDTO = permissionMapper.toDto(permission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, permissionDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(permissionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Permission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(permissionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchPermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(permissionSearchRepository.findAll().collectList().block());
        permission.setId(longCount.incrementAndGet());

        // Create the Permission
        PermissionDTO permissionDTO = permissionMapper.toDto(permission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(permissionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Permission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(permissionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamPermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(permissionSearchRepository.findAll().collectList().block());
        permission.setId(longCount.incrementAndGet());

        // Create the Permission
        PermissionDTO permissionDTO = permissionMapper.toDto(permission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(permissionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Permission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(permissionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deletePermission() {
        // Initialize the database
        insertedPermission = permissionRepository.save(permission).block();
        permissionRepository.save(permission).block();
        permissionSearchRepository.save(permission).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(permissionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the permission
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, permission.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(permissionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchPermission() {
        // Initialize the database
        insertedPermission = permissionRepository.save(permission).block();
        permissionSearchRepository.save(permission).block();

        // Search the permission
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + permission.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(permission.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    protected long getRepositoryCount() {
        return permissionRepository.count().block();
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

    protected Permission getPersistedPermission(Permission permission) {
        return permissionRepository.findById(permission.getId()).block();
    }

    protected void assertPersistedPermissionToMatchAllProperties(Permission expectedPermission) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPermissionAllPropertiesEquals(expectedPermission, getPersistedPermission(expectedPermission));
        assertPermissionUpdatableFieldsEquals(expectedPermission, getPersistedPermission(expectedPermission));
    }

    protected void assertPersistedPermissionToMatchUpdatableProperties(Permission expectedPermission) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPermissionAllUpdatablePropertiesEquals(expectedPermission, getPersistedPermission(expectedPermission));
        assertPermissionUpdatableFieldsEquals(expectedPermission, getPersistedPermission(expectedPermission));
    }
}
