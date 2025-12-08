import elements from './promotions-templates-components';


describe('Tests of Event/My Events new event creation', () => {
    context('using user entityMgrECVCy', () => {
        beforeEach(() => {
            cy.login('entityMgrECVCy');
            cy.visit('/event-promotion-templates');
        });
        it('creates a new (stubbed) event-promotion-template with entity user and gets redirected to it', () => {
            cy.intercept('POST', '**/event-promotion-templates', { 'id': 1876 }).as('POSTpromoTemplate');
            cy.get(elements.NEW_PROMO_TEMPLATE).click();
            cy.get(elements.CREATE.NAME)
                .type('Cypress ECV Template')
                .should('have.value', 'Cypress ECV Template');
            cy.get(elements.CREATE.TYPE_RADIO)
                .eq(1)
                .click()
                .should('have.class', 'mat-mdc-radio-checked');
            cy.get(elements.CREATE.MANUAL_TYPE_RADIO)
                .first()
                .click()
                .should('have.class', 'mat-mdc-radio-checked');
            cy.get(elements.CREATE.CREATE_BUTTON)
                .click();
            cy.wait('@POSTpromoTemplate').then(xhr => {
                expect(xhr.request.body).to.eql(
                    {
                        entity_id: 729,
                        name: 'Cypress ECV Template',
                        type: 'PROMOTION'
                    }
                );
            });
            cy.url().should('include', '/event-promotion-templates/1876/general-data');
        })
    })
})
