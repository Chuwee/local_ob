import elements from './user-details-components';
import $dialogs from '../../shared/components/dialogs-components';
import $buttons from '../../shared/components/buttons-components';

describe('Test user detail', () => {
    context('Using shiUser user', () => {
        let user;
        before(() => {
            cy.getFromFixture('users', 'editUser').then(u => { user = u })
        });
        beforeEach(() => {
            cy.login('shiUser')
            cy.intercept('PUT', '**/users/*', {}).as('userPUT');
            cy.visit(`/users/${user.id}/general-data`);
        })

        it('Change editUser name', () => {
            cy.get(elements.USER_NAME).type('!');
            cy.get($buttons.BUTTON_OK).click();
            cy.wait('@userPUT').then(xhr => {
                expect(xhr.request.body).to.eql(
                    {
                        'name': 'Lídia Prova -NO BORRAR-!',
                        'role': 'USER',
                        'status': 'ACTIVE',
                        'surname': '   ',
                        'username': 'prova@prova.com'
                    }
                )
            });
        })
        it('Change editUser surname', () => {
            cy.get(elements.USER_SURNAME).type('!');
            cy.get($buttons.BUTTON_OK).click();
            cy.wait('@userPUT').then(xhr => {
                expect(xhr.request.body).to.eql(
                    {
                        'name': 'Lídia Prova -NO BORRAR-',
                        'role': 'USER',
                        'status': 'ACTIVE',
                        'surname': '   !',
                        'username': 'prova@prova.com'
                    }
                )
            });
        })
        it('Change user status', () => {
            cy.getLiteral('USER.STATUS_OPTS.DISABLED').then(literal => {
                cy.get(elements.STATUS_SELECT, { timeout: 10000 }).first().click();
                cy.contains(elements.STATUS_SELECT_OPTION, literal).click();
                cy.get($buttons.BUTTON_OK).click();
                cy.wait('@userPUT').then(xhr => {
                    expect(xhr.request.body).to.eql(
                        {
                            'name': 'Lídia Prova -NO BORRAR-',
                            'role': 'USER',
                            'status': 'DISABLED',
                            'surname': '   ',
                            'username': 'prova@prova.com'
                        }
                    )
                })
                cy.get($dialogs.SNACKBAR_SUCCESS).should('be.visible');
            });
        });
        it('Change user role', () => {
            cy.getLiteral('USERS.ROLE_OPTS.ADMIN').then(literal => {
                cy.get(elements.ROLE_SELECT, { timeout: 10000 }).first().click();
                cy.contains(elements.STATUS_SELECT_OPTION, literal).click();
                cy.get($buttons.BUTTON_OK).click();
                cy.wait('@userPUT').then(xhr => {
                    expect(xhr.request.body).to.eql(
                        {
                            'name': 'Lídia Prova -NO BORRAR-',
                            'role': 'ADMIN',
                            'status': 'ACTIVE',
                            'surname': '   ',
                            'username': 'prova@prova.com'
                        }
                    )
                })
                cy.get($dialogs.SNACKBAR_SUCCESS).should('be.visible');
            });
        });
    })
    context('Using Role Admin user', () => {
        let user,
            admin;
        before(() => {
            cy.getFromFixture('users', 'editUser').then(u => { user = u })
            cy.getFromFixture('users', 'userTypeAdmin').then(a => { admin = a })
        });
        beforeEach(() => {
            cy.login('adminUser')
            cy.intercept('PUT', '**/users/*', {}).as('userPUT');
            cy.visit(`/users/${user.id}/general-data`);
        })

        it('Edits User type', () => {
            cy.get(elements.USER_NAME).type('!');
            cy.get($buttons.BUTTON_OK).click();
            cy.wait('@userPUT').then(xhr => {
                expect(xhr.request.body).to.eql(
                    {
                        'name': 'Lídia Prova -NO BORRAR-!',
                        'role': 'USER',
                        'status': 'ACTIVE',
                        'surname': '   ',
                        'username': 'prova@prova.com'
                    }
                )
            });
        })
        it('Sees admin type and button is disabled', () => {
            cy.visit(`/users/${admin.id}/general-data`);
            cy.url().should('include', 'f9eda6196a174dc6a5850ae3a2d90de4');
            cy.get($buttons.BUTTON_OK).should('be.disabled')
        })
    })
});
