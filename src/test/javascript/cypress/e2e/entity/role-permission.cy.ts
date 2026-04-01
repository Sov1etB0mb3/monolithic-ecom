import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('RolePermission e2e test', () => {
  const rolePermissionPageUrl = '/role-permission';
  const rolePermissionPageUrlPattern = new RegExp('/role-permission(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const rolePermissionSample = {};

  let rolePermission;
  let role;
  let permission;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/roles',
      body: { name: 'warmhearted huzzah', description: 'under clamor' },
    }).then(({ body }) => {
      role = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/permissions',
      body: { name: 'amount pigpen', description: 'jaunty' },
    }).then(({ body }) => {
      permission = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/role-permissions+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/role-permissions').as('postEntityRequest');
    cy.intercept('DELETE', '/api/role-permissions/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/roles', {
      statusCode: 200,
      body: [role],
    });

    cy.intercept('GET', '/api/permissions', {
      statusCode: 200,
      body: [permission],
    });
  });

  afterEach(() => {
    if (rolePermission) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/role-permissions/${rolePermission.id}`,
      }).then(() => {
        rolePermission = undefined;
      });
    }
  });

  afterEach(() => {
    if (role) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/roles/${role.id}`,
      }).then(() => {
        role = undefined;
      });
    }
    if (permission) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/permissions/${permission.id}`,
      }).then(() => {
        permission = undefined;
      });
    }
  });

  it('RolePermissions menu should load RolePermissions page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('role-permission');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('RolePermission').should('exist');
    cy.url().should('match', rolePermissionPageUrlPattern);
  });

  describe('RolePermission page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(rolePermissionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create RolePermission page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/role-permission/new$'));
        cy.getEntityCreateUpdateHeading('RolePermission');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', rolePermissionPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/role-permissions',
          body: {
            ...rolePermissionSample,
            role,
            permission,
          },
        }).then(({ body }) => {
          rolePermission = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/role-permissions+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [rolePermission],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(rolePermissionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details RolePermission page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('rolePermission');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', rolePermissionPageUrlPattern);
      });

      it('edit button click should load edit RolePermission page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('RolePermission');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', rolePermissionPageUrlPattern);
      });

      it('edit button click should load edit RolePermission page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('RolePermission');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', rolePermissionPageUrlPattern);
      });

      it('last delete button click should delete instance of RolePermission', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('rolePermission').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', rolePermissionPageUrlPattern);

        rolePermission = undefined;
      });
    });
  });

  describe('new RolePermission page', () => {
    beforeEach(() => {
      cy.visit(`${rolePermissionPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('RolePermission');
    });

    it('should create an instance of RolePermission', () => {
      cy.get(`[data-cy="role"]`).select(1);
      cy.get(`[data-cy="permission"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        rolePermission = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', rolePermissionPageUrlPattern);
    });
  });
});
