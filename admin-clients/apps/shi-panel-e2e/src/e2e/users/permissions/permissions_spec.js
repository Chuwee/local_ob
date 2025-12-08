import elements from '../permissions/permissions-components';
import $buttons from '../../shared/components/buttons-components';

describe('Test user detail general data', () => {
    context('Using shiUser user', () => {
        let user;
        before(() => {
            cy.getFromFixture('users', 'editUser').then(u => { user = u })
        });
        beforeEach(() => {
            cy.login('shiUser')
            cy.intercept('PUT', `**/users/${user.id}/permissions`, {}).as('userPUT');
            cy.visit(`/users/${user.id}/permissions`);
        })

        it('Edits user permissions', () => {
            cy.get(elements.SALES_VIEW_CHECKBOX).click();
            cy.get($buttons.BUTTON_OK).click();
            cy.wait('@userPUT').then(xhr => {
                expect(xhr.request.body).to.eql(
                    {
                        permissions: [
                            'listing_read',
                            'mapping_read',
                            'matching_read',
                            'ingestor_read'
                        ]
                    }
                )
            });
            cy.get(elements.USERS_WRITE_CHECKBOX).should('have.class', 'mat-mdc-checkbox-disabled')
        })
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
            cy.intercept('PUT', `**/users/${user.id}/permissions`, {}).as('userPUT');
        })

        it('Delete sales view permission', () => {
            cy.visit(`/users/${user.id}/permissions`);
            cy.get(elements.SALES_VIEW_CHECKBOX).click();
            cy.get($buttons.BUTTON_OK).click();
            cy.wait('@userPUT').then(xhr => {
                expect(xhr.request.body).to.eql(
                    {
                        permissions: [
                            'listing_read',
                            'mapping_read',
                            'matching_read',
                            'ingestor_read'
                        ]
                    }
                )
            });
        })
        it('Sees admin type and button is disabled', () => {
            cy.visit(`/users/${admin.id}/permissions`);
            cy.url().should('include', 'f9eda6196a174dc6a5850ae3a2d90de4');
            cy.get(elements.SALES_VIEW_CHECKBOX).should('have.class', 'mat-mdc-checkbox-disabled')
            cy.get(elements.USERS_WRITE_CHECKBOX).should('have.class', 'mat-mdc-checkbox-disabled')
            cy.get($buttons.BUTTON_OK).should('be.disabled')
        })
    })
});
