import elements from './event-prices-components';
import $bottombar from '../../../shared/components/bottombar-components';

describe('Tests of Event/Prices Tiers panel', () => {
    context('with user eventMgrEventCy in tiersEvent', () => {
        let event;
        beforeEach(() => {
            cy.login('eventMgrEventCy');
            cy.fixture('events').then((events) => {
                event = events.tiersEvent;
                cy.intercept('GET', `**/events/${event.id}`).as('GETevent');
                cy.intercept('PUT', '**/prices', {
                    status: 204,
                    response: {},
                }).as('PUTprices');
                cy.visit(`/events/${event.id}/prices`);
            });
        });
        it('adds a new tier', () => {
            cy.get(elements.ADD_TIER_BUTTON).first().click();
            cy.get(elements.TIER_NAME).eq(1).should('not.have.class', 'mat-form-field-disabled');
            cy.get(elements.TIER_NAME).eq(1).type('Tier Test');
            cy.get(elements.TIER_TYPE).eq(1).should('have.class', 'mat-form-field-disabled');
            cy.get(elements.TIER_PRICE).eq(1).type(10);
            cy.intercept('POST', `**/events/${event.id}/tiers`, {
                status: 201,
            }).as('POSTtier');
            cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED).click();
            cy.wait('@POSTtier').then((xhr) => {
                expect(xhr.request.body).to.have.all.keys(
                    'name',
                    'price',
                    'price_type_id',
                    'start_date'
                );
                expect(xhr.request.body).to.have.property('name', 'Tier Test');
                expect(xhr.request.body).to.have.property('price', 10);
            });
        });
        it('edits a tier', () => {
            cy.get(elements.TIER_NAME).first().type(' Test');
            cy.get(elements.TIER_TYPE).first().click();
            cy.getElByKey('EVENTS.TIERS.TABLE_STOCK_OR_DATE', 'mat-option').first().click();
            cy.get(elements.TIER_PRICE).first().type(20);
            cy.get(elements.TIER_SELL_LIMIT)
                .first()
                .should('have.class', 'ng-invalid');
            cy.get(elements.TIER_SELL_LIMIT).first().type(100);
            cy.intercept('PUT', `**/events/${event.id}/tiers/**`, {
                status: 204,
            }).as('PUTtier');
            cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED).click();
            cy.wait('@PUTtier').then((xhr) => {
                expect(xhr.request.body).to.have.all.keys(
                    'name',
                    'price',
                    'limit',
                    'on_sale',
                    'condition'
                );
                expect(xhr.request.body).to.have.property(
                    'name',
                    'Base Tier Test'
                );
                expect(xhr.request.body).to.eql({
                    condition: 'STOCK_OR_DATE',
                    limit: '100',
                    name: 'Base Tier Test',
                    on_sale: true,
                    price: 20,
                });
            });
        });
    });
});
