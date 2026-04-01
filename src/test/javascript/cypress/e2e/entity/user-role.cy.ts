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

describe('UserRole e2e test', () => {
  const userRolePageUrl = '/user-role';
  const userRolePageUrlPattern = new RegExp('/user-role(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const userRoleSample = {};

  let userRole;
  let user;
  let role;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/users',
      body: {
        username: 'ha yahoo apud',
        password: 'emboss out although',
        createdAt: '2026-03-31T21:40:33.554Z',
        updatedAt: '2026-03-31T15:50:08.507Z',
      },
    }).then(({ body }) => {
      user = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/roles',
      body: { name: 'yum massage', description: 'phew' },
    }).then(({ body }) => {
      role = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/user-roles+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/user-roles').as('postEntityRequest');
    cy.intercept('DELETE', '/api/user-roles/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [user],
    });

    cy.intercept('GET', '/api/roles', {
      statusCode: 200,
      body: [role],
    });
  });

  afterEach(() => {
    if (userRole) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/user-roles/${userRole.id}`,
      }).then(() => {
        userRole = undefined;
      });
    }
  });

  afterEach(() => {
    if (user) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/users/${user.id}`,
      }).then(() => {
        user = undefined;
      });
    }
    if (role) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/roles/${role.id}`,
      }).then(() => {
        role = undefined;
      });
    }
  });

  it('UserRoles menu should load UserRoles page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('user-role');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('UserRole').should('exist');
    cy.url().should('match', userRolePageUrlPattern);
  });

  describe('UserRole page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(userRolePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create UserRole page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/user-role/new$'));
        cy.getEntityCreateUpdateHeading('UserRole');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userRolePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/user-roles',
          body: {
            ...userRoleSample,
            user,
            role,
          },
        }).then(({ body }) => {
          userRole = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/user-roles+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [userRole],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(userRolePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details UserRole page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('userRole');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userRolePageUrlPattern);
      });

      it('edit button click should load edit UserRole page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('UserRole');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userRolePageUrlPattern);
      });

      it('edit button click should load edit UserRole page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('UserRole');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userRolePageUrlPattern);
      });

      it('last delete button click should delete instance of UserRole', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('userRole').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userRolePageUrlPattern);

        userRole = undefined;
      });
    });
  });

  describe('new UserRole page', () => {
    beforeEach(() => {
      cy.visit(`${userRolePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('UserRole');
    });

    it('should create an instance of UserRole', () => {
      cy.get(`[data-cy="user"]`).select(1);
      cy.get(`[data-cy="role"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        userRole = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', userRolePageUrlPattern);
    });
  });
});
