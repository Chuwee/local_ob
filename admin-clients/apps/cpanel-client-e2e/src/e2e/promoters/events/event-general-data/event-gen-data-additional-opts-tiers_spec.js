import elements from './event-gen-data-components';
import $bottombar from '../../../shared/components/bottombar-components';

describe('Tests of Event/General Data/Additional Options with user operAdminCy', () => {
    let eventWithTiers,
        eventWithoutTiers,
        eventUnableToHaveTiers;
    before(() => {
        cy.fixture('events').then(events => {
            eventWithTiers = events.tiersEvent;
            eventWithoutTiers = events.eventWithoutChannels;
            eventUnableToHaveTiers = events.eventSimpleEvent;
        });
    });
    context('Tiers Panel', () => {
        beforeEach(() => {
            cy.login('operAdminCy');
            cy.intercept('GET', '**/events/*').as('GETevent');
            cy.intercept('PUT', '**/events/*', { status: 204, response: {} }).as('PUTevent');
        });
        it('shows Tiers config disabled', () => {
            cy.visit(`/events/${eventWithTiers.id}/general-data/additional-options`);
            cy.wait('@GETevent').then(xhr => {
                expect(xhr.response.body.settings).to.have.property('use_tiered_pricing', true);
            })
            cy.get(elements.TIERS_CHECKBOX)
                .should('have.class', 'mat-mdc-checkbox-checked')
                .and('have.class', 'mat-mdc-checkbox-disabled');
            cy.visit(`/events/${eventUnableToHaveTiers.id}/general-data/additional-options`);
            cy.wait('@GETevent').then(xhr => {
                expect(xhr.response.body.settings).to.have.property('use_tiered_pricing', false);
            })
            cy.get(elements.TIERS_CHECKBOX)
                .should('have.class', 'mat-mdc-checkbox-disabled');
        });
        it('shows Tiers config enabled', () => {
            cy.visit(`/events/${eventWithoutTiers.id}/general-data/additional-options`);
            cy.wait('@GETevent').then(xhr => {
                expect(xhr.response.body.settings).to.have.property('use_tiered_pricing', false);
            })
            cy.get(elements.TIERS_CHECKBOX_INPUT)
                .click()
                .parents(elements.TIERS_CHECKBOX)
                .should('have.class', 'mat-mdc-checkbox-checked');
            cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED).click();
            cy.wait('@PUTevent').then(xhr => {
                expect(xhr.request.body.settings).to.have.property('use_tiered_pricing', true);
            });
        })
    });
});
