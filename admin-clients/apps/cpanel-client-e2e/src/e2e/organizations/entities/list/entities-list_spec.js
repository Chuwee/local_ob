import $menu from '../../../shared/components/menu-components';

describe('Tests of Organizations/Entities/Entities List', () => {
    const base = Cypress.config('baseUrl');
    before(() => {
        cy.intercept('GET', '**/mgmt-api/v1/entities?**').as('SearchEntities')
    });
    context('with user operAdminCy', () => {
        beforeEach(() => {
            cy.login('operAdminCy');
            cy.visit('/entities');
        });
        it('sees a list of all entities and organizations menu with all options', () => {
            cy.wait('@SearchEntities', { timeout: 10000 }).then(xhr => {
                // expect(xhr.request.url).to.contain('include_entity_admin'); TODO: uncomment when backend is ready
                cy.get('mat-row').should('have.length', xhr.response.body.data.length);
            });
            cy.contains($menu.COLLAPSED_ITEM, 'account_box').click();
            cy.get($menu.POPOVER_ITEM).spread((i1, i2, i3, i4) => {
                expect(i1.href).to.match(/\/my-entity/);
                expect(i2.href).to.match(/\/entities/);
                expect(i3.href).to.match(/\/my-user/);
                expect(i4.href).to.match(/\/users/);
            })
        });
    });
    context('with user ecvMgrECVCy', () => {
        beforeEach(() => {
            cy.login('ecvMgrECVCy');
            cy.visit('/entities');
        });
        it('sees not a list of all entities and organizations menu with own entity option only', () => {
            cy.url().should('not.contain', 'entities');
            cy.contains($menu.COLLAPSED_ITEM, 'account_box').click();
            cy.get($menu.POPOVER_ITEM).should(i => {
                expect(i).to.have.length(1);
                expect(i[0].href).to.match(/\/my-user/);
            })
        });
    });
    context('with user entityMgrECVCy', () => {
        beforeEach(() => {
            cy.login('entityMgrECVCy');
            cy.visit('/entities');
        });
        it('sees not a list of all entities and organizations menu with own entity and entity users options', () => {
            cy.url().should('not.contain', 'entities');
            cy.contains($menu.COLLAPSED_ITEM, 'account_box').click();
            cy.get($menu.POPOVER_ITEM).spread((i1, i2, i3) => {
                expect(i1.href).to.match(/\/my-entity/);
                expect(i2.href).to.match(/\/my-user/);
                expect(i3.href).to.match(/\/users/);
            })
        });
    });
});
