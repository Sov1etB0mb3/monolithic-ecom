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

describe('User e2e test', () => {
  const userPageUrl = '/user';
  const userPageUrlPattern = new RegExp('/user(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const userSample = { username: 'linseed', password: 'mmm' };

  let user;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/users+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/users').as('postEntityRequest');
    cy.intercept('DELETE', '/api/users/*').as('deleteEntityRequest');
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
  });

  it('Users menu should load Users page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('user');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('User').should('exist');
    cy.url().should('match', userPageUrlPattern);
  });

  describe('User page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(userPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create User page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/user/new$'));
        cy.getEntityCreateUpdateHeading('User');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/users',
          body: userSample,
        }).then(({ body }) => {
          user = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/users+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/users?page=0&size=20>; rel="last",<http://localhost/api/users?page=0&size=20>; rel="first"',
              },
              body: [user],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(userPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details User page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('user');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userPageUrlPattern);
      });

      it('edit button click should load edit User page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('User');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userPageUrlPattern);
      });

      it('edit button click should load edit User page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('User');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userPageUrlPattern);
      });

      it('last delete button click should delete instance of User', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('user').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userPageUrlPattern);

        user = undefined;
      });
    });
  });

  describe('new User page', () => {
    beforeEach(() => {
      cy.visit(`${userPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('User');
    });

    it('should create an instance of User', () => {
      cy.get(`[data-cy="username"]`).type('gadzooks awareness');
      cy.get(`[data-cy="username"]`).should('have.value', 'gadzooks awareness');

      cy.get(`[data-cy="password"]`).type('fragrant whereas what');
      cy.get(`[data-cy="password"]`).should('have.value', 'fragrant whereas what');

      cy.get(`[data-cy="createdAt"]`).type('2026-04-01T04:31');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2026-04-01T04:31');

      cy.get(`[data-cy="updatedAt"]`).type('2026-03-31T15:05');
      cy.get(`[data-cy="updatedAt"]`).blur();
      cy.get(`[data-cy="updatedAt"]`).should('have.value', '2026-03-31T15:05');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        user = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', userPageUrlPattern);
    });
  });
});
