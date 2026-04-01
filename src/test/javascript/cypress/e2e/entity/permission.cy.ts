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

describe('Permission e2e test', () => {
  const permissionPageUrl = '/permission';
  const permissionPageUrlPattern = new RegExp('/permission(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const permissionSample = { name: 'once lifestyle' };

  let permission;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/permissions+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/permissions').as('postEntityRequest');
    cy.intercept('DELETE', '/api/permissions/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (permission) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/permissions/${permission.id}`,
      }).then(() => {
        permission = undefined;
      });
    }
  });

  it('Permissions menu should load Permissions page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('permission');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Permission').should('exist');
    cy.url().should('match', permissionPageUrlPattern);
  });

  describe('Permission page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(permissionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Permission page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/permission/new$'));
        cy.getEntityCreateUpdateHeading('Permission');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', permissionPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/permissions',
          body: permissionSample,
        }).then(({ body }) => {
          permission = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/permissions+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/permissions?page=0&size=20>; rel="last",<http://localhost/api/permissions?page=0&size=20>; rel="first"',
              },
              body: [permission],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(permissionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Permission page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('permission');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', permissionPageUrlPattern);
      });

      it('edit button click should load edit Permission page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Permission');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', permissionPageUrlPattern);
      });

      it('edit button click should load edit Permission page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Permission');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', permissionPageUrlPattern);
      });

      it('last delete button click should delete instance of Permission', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('permission').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', permissionPageUrlPattern);

        permission = undefined;
      });
    });
  });

  describe('new Permission page', () => {
    beforeEach(() => {
      cy.visit(`${permissionPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Permission');
    });

    it('should create an instance of Permission', () => {
      cy.get(`[data-cy="name"]`).type('spotless over');
      cy.get(`[data-cy="name"]`).should('have.value', 'spotless over');

      cy.get(`[data-cy="description"]`).type('instead blah');
      cy.get(`[data-cy="description"]`).should('have.value', 'instead blah');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        permission = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', permissionPageUrlPattern);
    });
  });
});
