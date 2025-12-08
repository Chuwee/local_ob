import elements from './users-list-components';
import $chips from '../../../shared/components/chips-components';
import $dialogs from '../../../shared/components/dialogs-components';
import $filters from '../../../shared/components/filter-components';


describe('Tests of Organizations/Entity users list', () => {
    context('using user operAdminCy and entity Cypress ECV', () => {
        let entity,
            user;
        before(() => {
            cy.getFromFixture('organizations/entities', 'CyECV').then(ent => { entity = ent })
            cy.getFromFixture('users', 'tempBlocked').then(u => { user = u })
        });
        beforeEach(() => {
            cy.login('operAdminCy');
            cy.intercept(/\/users\?(?!branch|sp)./).as('GETusers');
            cy.visit('/users');
        });
        it('sees entity column with operator user and list is sorted by name descending', () => {
            cy.wait('@GETusers').then((xhr) => {
                expect(xhr.request.url).to.contain('sort=name%3Aasc');
            });
            cy.get(elements.HEADER_IN_ENTITY_COL).should('be.visible');
            cy.get(elements.HEADER_IN_NAME_COL).should('have.attr', 'aria-sort', 'ascending');
        });
        it('applies an option on the Entity filter', () => {
            cy.intercept('**/users?*entity_id*').as('GETuserByEntity');
            cy.get(elements.OPEN_FILTER_BUTTON).click();
            cy.get(elements.FILTER_ENTITY_SELECT).click();
            cy.contains(elements.DROPDOWN_OPTION, entity.name).click();
            cy.get($filters.FILTER_APPLY_BTN).focus().click();
            cy.contains($chips.CHIP, entity.name).should('be.visible');
            cy.wait('@GETuserByEntity')
                .its('request.url')
                .should('contain', `entity_id=${entity.id}`);
        });
        it('creates a new user for an entity', () => {
            cy.intercept('POST', '**/users', { id: user.id }).as('POSTuser');
            cy.get(elements.NEW_USER_BTN).click();
            cy.get(elements.NEW_USER_ENTITY).click();
            cy.contains(elements.DROPDOWN_OPTION, entity.name, { matchCase: true }).click();
            cy.get(elements.NEW_USER_ENTITY).should('have.text', entity.name);
            cy.get(elements.NEW_USER_NAME).type('Name');
            cy.get(elements.NEW_USER_SURNAME).type('Surname');
            cy.get(elements.NEW_USER_EMAIL).type('email@mail.com');
            cy.get(elements.NEW_USER_POSITION).type('Admin');
            cy.get(elements.NEW_USER_CREATE_BTN).click();
            cy.wait('@POSTuser').then(xhr => {
                expect(xhr.request.body).to.eql(
                    {
                        'username': 'email@mail.com',
                        'entity_id': entity.id,
                        'name': 'Name',
                        'last_name': 'Surname',
                        'job_title': 'Admin'
                    }
                )
            });
            cy.url().should('contain', user.id);
        });
        it('changes user status', () => {
            cy.intercept('PUT', '**/users/*', {}).as('userPUT');
            cy.get(elements.STATUS_SELECT, { timeout: 10000 }).first().click();
            cy.getLiteral('USER.STATUS_OPTS.BLOCKED').then(literal => {
                cy.contains(elements.STATUS_SELECT_OPTION, literal).click();
                cy.wait('@userPUT').then(xhr => {
                    expect(xhr.request.body).to.eql({ status: 'BLOCKED' })
                })
                cy.get($dialogs.SNACKBAR_SUCCESS).should('be.visible');
            })
        });
        it('requests password regeneration of a user', () => {
            cy.intercept('POST', '**/users/forgot-password', { statusCode: 202 }).as('POSTforgotPass');
            cy.get(elements.ACTION_REGENERATE_PASSWORD_BUTTON, { timeout: 10000 }).first().click({ force: true });
            cy.get($dialogs.ALERT_BUTTON_CONFIRM).click()
            cy.wait(['@POSTforgotPass', '@GETusers']).spread(
                (forgot, users) => {
                    expect(forgot.request.body).to.eql(
                        { email: users.response.body.data[0].email }
                    )
                })
            cy.get($dialogs.SNACKBAR_SUCCESS).should('be.visible');
        });
        it('deletes a user', () => {
            cy.intercept('DELETE', '**/users/*', { statusCode: 204 }).as('DELETEuser');
            cy.get(elements.ACTION_DELETE_USER_BUTTON, { timeout: 10000 }).first().click({ force: true });
            cy.get($dialogs.ALERT_BUTTON_CONFIRM).click();
            cy.wait(['@DELETEuser', '@GETusers']).spread(
                (del, user) => {
                    expect(del.request.url).to.contain(user.response.body.data[0].id)
                });
            cy.get($dialogs.SNACKBAR_SUCCESS).should('be.visible');
        });
    });
    context('using user entityMgrECVCy', () => {
        beforeEach(() => {
            cy.login('entityMgrECVCy');
            cy.intercept(/\/users\?(?!branch|sp)./).as('GETusers');
            cy.visit('/users');
        });
        it('sees not entity column with entity user and list is sorted by name descending', () => {
            cy.wait('@GETusers').then((xhr) => {
                expect(xhr.request.url).to.contain('sort=name%3Aasc');
            });
            cy.get(elements.HEADER_IN_ENTITY_COL).should('not.exist');
            cy.get(elements.HEADER_IN_NAME_COL).should('have.attr', 'aria-sort', 'ascending');
        });
        it('applies an option on the Status filter and a chip with the filter is shown', () => {
            cy.wait('@translations').then(xhr => {
                const translations = xhr.response.body;
                const chipLabel = translations.FORMS.LABELS.STATUS;
                const statusTempBloq = translations.USER.STATUS_OPTS.TEMPORARY_BLOCKED;
                cy.intercept('**users?*status=TEMPORARY_BLOCKED**').as('searchTempBlocked');
                cy.get(elements.OPEN_FILTER_BUTTON).click();
                cy.get(elements.FILTER_CHECKBOX_TEMP_BLOCKED)
                    .click()
                    .should('have.class', 'mat-mdc-checkbox-checked');
                cy.get($filters.FILTER_APPLY_BTN).focus().click();
                cy.wait('@searchTempBlocked')
                    .its('request.url')
                    .should('contain', 'status=TEMPORARY_BLOCKED')
                    .and('contain', 'sort=name%3Aasc')
                    .and('contain', 'limit=20')
                    .and('contain', 'offset=0');
                cy.get($chips.CHIP)
                    .should('have.length', 1)
                    .and('contain', `${chipLabel}: ${statusTempBloq}`);
            })
        });
    });
});
