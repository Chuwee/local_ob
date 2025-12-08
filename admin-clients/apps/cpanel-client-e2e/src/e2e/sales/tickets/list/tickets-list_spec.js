import elements from './tickets-list-components';
import $topbar from '../../../shared/components/topbar-components';

describe('Tests of Tickets List', () => {
    context('using user operAdminQaa', () => {
        beforeEach(() => {
            cy.login('operAdminQaa');
            cy.intercept(/\/order-items\?(?!branch|sp)./).as('ticketsSearch');
            cy.visit('/tickets?state=PURCHASE&noDate=true');
        });
        it('get list sorted by date and search a ticket by order code', { tags: '@pgl' }, () => {
            cy.wait('@ticketsSearch').then(xhr => {
                expect(xhr.request.url).to.contain('sort=purchase_date%3Adesc');
                expect(xhr.request.url).to.contain('state=PURCHASE');
                const orderCode = xhr.response.body.data[0].order.code;
                cy.get(elements.SEARCH_INPUT)
                    .type(`${orderCode}{enter}`);

                cy.wait('@ticketsSearch').then(xhrTicket => {
                    expect(xhrTicket.request.url).to.contain(`q=${orderCode}`);
                    cy.get(elements.ORDER_CODE_CELL)
                        .first()
                        .should('contain', orderCode)
                        .click({ force: true });
                });

                cy.get('.heading-container h1').should('contain', orderCode);
            })
        });
    });
    context('using user ecvMgrECVCy', () => {
        beforeEach(() => {
            cy.login('ecvMgrECVCy');
            cy.intercept(/\/order-items\?(?!branch|sp)./).as('ticketsSearch');
            cy.visit('/tickets?state=PURCHASE&noDate=true');
        });
        it('sees entity name in topbar', () => {
            cy.wait('@myself').then(xhr => {
                const entityName = xhr.response.body.entity.name;
                cy.get($topbar.TOPBAR_ENTITY)
                    .should('include.text', entityName);
            })
        });
    });
});
