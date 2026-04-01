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

describe('Role e2e test', () => {
  const rolePageUrl = '/role';
  const rolePageUrlPattern = new RegExp('/role(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const roleSample = { name: 'superior winding mount' };

  let role;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/roles+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/roles').as('postEntityRequest');
    cy.intercept('DELETE', '/api/roles/*').as('deleteEntityRequest');
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
  });

  it('Roles menu should load Roles page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('role');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Role').should('exist');
    cy.url().should('match', rolePageUrlPattern);
  });

  describe('Role page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(rolePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Role page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/role/new$'));
        cy.getEntityCreateUpdateHeading('Role');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', rolePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/roles',
          body: roleSample,
        }).then(({ body }) => {
          role = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/roles+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/roles?page=0&size=20>; rel="last",<http://localhost/api/roles?page=0&size=20>; rel="first"',
              },
              body: [role],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(rolePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Role page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('role');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', rolePageUrlPattern);
      });

      it('edit button click should load edit Role page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Role');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', rolePageUrlPattern);
      });

      it('edit button click should load edit Role page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Role');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', rolePageUrlPattern);
      });

      it('last delete button click should delete instance of Role', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('role').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', rolePageUrlPattern);

        role = undefined;
      });
    });
  });

  describe('new Role page', () => {
    beforeEach(() => {
      cy.visit(`${rolePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Role');
    });

    it('should create an instance of Role', () => {
      cy.get(`[data-cy="name"]`).type('round psst hmph');
      cy.get(`[data-cy="name"]`).should('have.value', 'round psst hmph');

      cy.get(`[data-cy="description"]`).type('sometimes');
      cy.get(`[data-cy="description"]`).should('have.value', 'sometimes');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        role = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', rolePageUrlPattern);
    });
  });
});
