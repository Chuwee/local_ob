import elements from './users-list-components';
import $dialogs from '../../shared/components/dialogs-components';
import $buttons from '../../shared/components/buttons-components';
import $filters from '../../shared/components/filters-components';

describe('Test users list', () => {
    context('Using shiUser user', () => {
        let user,
            role;
        before(() => {
            cy.getFromFixture('users', 'newUser').then(u => { user = u })
            cy.getFromFixture('users/roles', 'user').then(rol => { role = rol })
        });
        beforeEach(() => {
            cy.login('shiUser')
            cy.intercept(/\/v1\/users\?(?!branch|sp)./).as('GETusers')
            cy.visit('/users')
        })
        it('Get list sorted by name', { tags: '@pgl' }, () => {
            cy.wait('@GETusers', { timeout: 10000 }).then((xhr) => {
                expect(xhr.request.url).to.contain('sort=name%3Adesc')

                // Validate all items
                let users = xhr.response.body.data
                cy.get(elements.USERS_LIST_ROW)
                    .should('have.length', users.length)
                    .each(($el, index) => {
                        cy.wrap($el).find(elements.COLUMN_USER_NAME).contains(users[index].name)
                    })
            })
        })
        it('Change the sorting in the users list', () => {
            cy.wait('@GETusers')
                .its('request.url')
                .should('contain', 'limit=20')
                .and('contain', 'aggs=true')
                .and('contain', 'offset=0')
                .and('contain', 'sort=name%3Adesc')
            cy.get(elements.COLUMN_USER_NAME).find($buttons.BUTTON_SORT_ARROW).click()
            cy.wait('@GETusers', { timeout: 10000 }).then((xhr) => {
                expect(xhr.request.url).to.contain('sort=name%3Aasc')

                let users = xhr.response.body.data
                cy.get(elements.USERS_LIST_ROW)
                    .should('have.length', users.length)
                    .each(($el, index) => {
                        cy.wrap($el).find(elements.COLUMN_USER_NAME).contains(users[index].name)
                    })
            })
        })
        it('Create new user', () => {
            cy.intercept('POST', '**/users', { id: user.id }).as('POSTuser');
            cy.get(elements.NEW_USER_BTN).click();
            cy.get(elements.NEW_USER_NAME).type('Name');
            cy.get(elements.NEW_USER_SURNAME).type('Surname');
            cy.get(elements.NEW_USER_EMAIL).type('email@mail.com');
            cy.get(elements.NEW_USER_ROLE).click();
            cy.contains($filters.DROPDOWN_OPTION, role.name, { matchCase: true }).click();
            cy.get(elements.NEW_USER_ROLE).should('have.text', role.name);
            cy.get(elements.NEW_USER_CREATE_BTN).click();
            cy.wait('@POSTuser').then(xhr => {
                expect(xhr.request.body).to.eql(
                    {
                        'name': 'Name',
                        'surname': 'Surname',
                        'username': 'email@mail.com',
                        'role': 'USER'
                    }
                )
            });
        })
        it('changes user status', () => {
            cy.intercept('PUT', '**/users/*', { statusCode: 204 }).as('userPUT');
            cy.getLiteral('USER.STATUS_OPTS.DISABLED').then(literal => {
                cy.get(elements.STATUS_SELECT, { timeout: 10000 }).first().click();
                cy.contains(elements.STATUS_SELECT_OPTION, literal).click();
                cy.wait('@userPUT').then(xhr => {
                    expect(xhr.request.body).to.eql({ status: 'DISABLED' })
                })
                cy.get($dialogs.SNACKBAR_SUCCESS).should('be.visible');

            })
        });
        it('deletes a user', () => {
            cy.intercept('DELETE', '**/users/*', { statusCode: 204 }).as('DELETEuser');
            cy.get(elements.ACTION_DELETE_USER_BUTTON, { force: true, timeout: 10000 }).first().click({ force: true });
            cy.get($dialogs.ALERT_BUTTON_CONFIRM).click();
            cy.wait(['@DELETEuser', '@GETusers']).spread(
                (del, user) => {
                    expect(del.request.url).to.contain(user.response.body.data[0].id)
                });
            cy.get($dialogs.SNACKBAR_SUCCESS).should('be.visible');
        });
    })
})
