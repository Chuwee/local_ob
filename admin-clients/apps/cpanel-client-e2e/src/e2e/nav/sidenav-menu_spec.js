import elements from '../shared/components/menu-components';

describe('Tests of Main Menu', () => {
    const base = Cypress.config('baseUrl');
    context('using user operAdminCy', () => {
        beforeEach(() => {
            cy.login('operAdminCy');
        });
        it('sees menu closed and opens it', () => {
            window.localStorage.setItem('ob-sidenav-opened', false);
            cy.visit('/users');
            cy.get(elements.EXPANDED_MENU).should('not.be.visible');
            cy.get(elements.COLLAPSED_MENU).should('be.visible');
            cy.get(elements.MENU_BUTTON).click();
            cy.get(elements.EXPANDED_MENU).should('be.visible');
            cy.get(elements.COLLAPSED_MENU).should('not.be.visible');
            cy.wrap(window.localStorage).should('have.property', 'ob-sidenav-opened', 'true')
        });
        it('sees sales/transactions and tickets navigation buttons', () => {
            cy.visit('/transactions');
            cy.contains(elements.COLLAPSED_ITEM, 'trending_up').click();
            cy.get(elements.POPOVER_ITEM).spread((i1, i2, i3) => {
                expect(i1.href).to.match(/\/transactions/);
                expect(i2.href).to.match(/\/tickets/);
                expect(i3.href).to.match(/\/voucher-orders/);
            })
        });
        it('sees users/entities and own user/entity details navigation buttons', () => {
            cy.visit('/users');
            cy.contains(elements.COLLAPSED_ITEM, 'account_box').click();
            cy.get(elements.POPOVER_ITEM).spread((i1, i2, i3, i4) => {
                expect(i1.href).to.match(/\/my-entity/);
                expect(i2.href).to.match(/\/entities/);
                expect(i3.href).to.match(/\/my-user/);
                expect(i4.href).to.match(/\/users/);
            })
        });
    });
    context('using user entityMgrECVCy', () => {
        beforeEach(() => {
            cy.login('entityMgrECVCy');
        });
        it('sees users and own user/entity details navigation buttons', () => {
            cy.visit('/users');
            cy.contains(elements.COLLAPSED_ITEM, 'account_box').click();
            cy.get(elements.POPOVER_ITEM).spread((i1, i2, i3) => {
                expect(i1.href).to.match(/\/my-entity/);
                expect(i2.href).to.match(/\/my-user/);
                expect(i3.href).to.match(/\/users/);
            })
        });
    });
    context('using user entityMgrEventCy', () => {
        beforeEach(() => {
            cy.login('entityMgrEventCy');
        });
        it('sees only sales/tickets navigation buttons', () => {
            cy.visit('/tickets');
            cy.contains(elements.COLLAPSED_ITEM, 'trending_up').click();
            cy.get(elements.POPOVER_ITEM).should(i => {
                expect(i).to.have.length(1);
                expect(i[0].href).to.match(/\/tickets/);
            })
        })
    });
});
