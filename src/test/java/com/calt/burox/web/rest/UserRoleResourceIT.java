package com.calt.burox.web.rest;

import static com.calt.burox.domain.UserRoleAsserts.*;
import static com.calt.burox.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.calt.burox.IntegrationTest;
import com.calt.burox.domain.Role;
import com.calt.burox.domain.User;
import com.calt.burox.domain.UserRole;
import com.calt.burox.repository.EntityManager;
import com.calt.burox.repository.UserRoleRepository;
import com.calt.burox.repository.search.UserRoleSearchRepository;
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
 * Integration tests for the {@link UserRoleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class UserRoleResourceIT {

    private static final String ENTITY_API_URL = "/api/user-roles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/user-roles/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRoleSearchRepository userRoleSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private UserRole userRole;

    private UserRole insertedUserRole;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserRole createEntity(EntityManager em) {
        UserRole userRole = new UserRole();
        // Add required entity
        User user = em.insert(UserResourceIT.createEntity()).block();
        userRole.setUser(user);
        // Add required entity
        Role role;
        role = em.insert(RoleResourceIT.createEntity()).block();
        userRole.setRole(role);
        return userRole;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserRole createUpdatedEntity(EntityManager em) {
        UserRole updatedUserRole = new UserRole();
        // Add required entity
        User user = em.insert(UserResourceIT.createEntity()).block();
        updatedUserRole.setUser(user);
        // Add required entity
        Role role;
        role = em.insert(RoleResourceIT.createUpdatedEntity()).block();
        updatedUserRole.setRole(role);
        return updatedUserRole;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(UserRole.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        UserResourceIT.deleteEntities(em);
        RoleResourceIT.deleteEntities(em);
    }

    @BeforeEach
    void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    void initTest() {
        userRole = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedUserRole != null) {
            userRoleRepository.delete(insertedUserRole).block();
            userRoleSearchRepository.delete(insertedUserRole).block();
            insertedUserRole = null;
        }
        deleteEntities(em);
    }

    @Test
    void createUserRole() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userRoleSearchRepository.findAll().collectList().block());
        // Create the UserRole
        var returnedUserRole = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userRole))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(UserRole.class)
            .returnResult()
            .getResponseBody();

        // Validate the UserRole in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertUserRoleUpdatableFieldsEquals(returnedUserRole, getPersistedUserRole(returnedUserRole));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(userRoleSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedUserRole = returnedUserRole;
    }

    @Test
    void createUserRoleWithExistingId() throws Exception {
        // Create the UserRole with an existing ID
        userRole.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userRoleSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userRole))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserRole in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userRoleSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllUserRolesAsStream() {
        // Initialize the database
        userRoleRepository.save(userRole).block();

        List<UserRole> userRoleList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(UserRole.class)
            .getResponseBody()
            .filter(userRole::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(userRoleList).isNotNull();
        assertThat(userRoleList).hasSize(1);
        UserRole testUserRole = userRoleList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertUserRoleAllPropertiesEquals(userRole, testUserRole);
        assertUserRoleUpdatableFieldsEquals(userRole, testUserRole);
    }

    @Test
    void getAllUserRoles() {
        // Initialize the database
        insertedUserRole = userRoleRepository.save(userRole).block();

        // Get all the userRoleList
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
            .value(hasItem(userRole.getId().intValue()));
    }

    @Test
    void getUserRole() {
        // Initialize the database
        insertedUserRole = userRoleRepository.save(userRole).block();

        // Get the userRole
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, userRole.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(userRole.getId().intValue()));
    }

    @Test
    void getNonExistingUserRole() {
        // Get the userRole
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingUserRole() throws Exception {
        // Initialize the database
        insertedUserRole = userRoleRepository.save(userRole).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        userRoleSearchRepository.save(userRole).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userRoleSearchRepository.findAll().collectList().block());

        // Update the userRole
        UserRole updatedUserRole = userRoleRepository.findById(userRole.getId()).block();

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedUserRole.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedUserRole))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserRole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserRoleToMatchAllProperties(updatedUserRole);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(userRoleSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<UserRole> userRoleSearchList = Streamable.of(userRoleSearchRepository.findAll().collectList().block()).toList();
                UserRole testUserRoleSearch = userRoleSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertUserRoleAllPropertiesEquals(testUserRoleSearch, updatedUserRole);
                assertUserRoleUpdatableFieldsEquals(testUserRoleSearch, updatedUserRole);
            });
    }

    @Test
    void putNonExistingUserRole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userRoleSearchRepository.findAll().collectList().block());
        userRole.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, userRole.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userRole))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserRole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userRoleSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchUserRole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userRoleSearchRepository.findAll().collectList().block());
        userRole.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userRole))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserRole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userRoleSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamUserRole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userRoleSearchRepository.findAll().collectList().block());
        userRole.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userRole))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the UserRole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userRoleSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateUserRoleWithPatch() throws Exception {
        // Initialize the database
        insertedUserRole = userRoleRepository.save(userRole).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userRole using partial update
        UserRole partialUpdatedUserRole = new UserRole();
        partialUpdatedUserRole.setId(userRole.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUserRole.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedUserRole))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserRole in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserRoleUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedUserRole, userRole), getPersistedUserRole(userRole));
    }

    @Test
    void fullUpdateUserRoleWithPatch() throws Exception {
        // Initialize the database
        insertedUserRole = userRoleRepository.save(userRole).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userRole using partial update
        UserRole partialUpdatedUserRole = new UserRole();
        partialUpdatedUserRole.setId(userRole.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUserRole.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedUserRole))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserRole in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserRoleUpdatableFieldsEquals(partialUpdatedUserRole, getPersistedUserRole(partialUpdatedUserRole));
    }

    @Test
    void patchNonExistingUserRole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userRoleSearchRepository.findAll().collectList().block());
        userRole.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, userRole.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(userRole))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserRole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userRoleSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchUserRole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userRoleSearchRepository.findAll().collectList().block());
        userRole.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(userRole))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserRole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userRoleSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamUserRole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userRoleSearchRepository.findAll().collectList().block());
        userRole.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(userRole))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the UserRole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userRoleSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteUserRole() {
        // Initialize the database
        insertedUserRole = userRoleRepository.save(userRole).block();
        userRoleRepository.save(userRole).block();
        userRoleSearchRepository.save(userRole).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userRoleSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the userRole
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, userRole.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userRoleSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchUserRole() {
        // Initialize the database
        insertedUserRole = userRoleRepository.save(userRole).block();
        userRoleSearchRepository.save(userRole).block();

        // Search the userRole
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + userRole.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(userRole.getId().intValue()));
    }

    protected long getRepositoryCount() {
        return userRoleRepository.count().block();
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

    protected UserRole getPersistedUserRole(UserRole userRole) {
        return userRoleRepository.findById(userRole.getId()).block();
    }

    protected void assertPersistedUserRoleToMatchAllProperties(UserRole expectedUserRole) {
        // Test fails because reactive api returns an empty object instead of null
        // assertUserRoleAllPropertiesEquals(expectedUserRole, getPersistedUserRole(expectedUserRole));
        assertUserRoleUpdatableFieldsEquals(expectedUserRole, getPersistedUserRole(expectedUserRole));
    }

    protected void assertPersistedUserRoleToMatchUpdatableProperties(UserRole expectedUserRole) {
        // Test fails because reactive api returns an empty object instead of null
        // assertUserRoleAllUpdatablePropertiesEquals(expectedUserRole, getPersistedUserRole(expectedUserRole));
        assertUserRoleUpdatableFieldsEquals(expectedUserRole, getPersistedUserRole(expectedUserRole));
    }
}
