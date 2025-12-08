import elements from './event-prices-components';
import dialogs from '../../../shared/components/dialogs-components';

describe('Tests of Event/Prices Rates panel', () => {
    context('with user eventMgrEventCy in pricesEvent', () => {
        let event;
        beforeEach(() => {
            cy.login('eventMgrEventCy');
            cy.fixture('events').then((events) => {
                event = events.pricesEvent;
                cy.intercept('GET', `**/events/${event.id}`).as('GETevent');
                cy.intercept('GET', `**/events/${event.id}/rates`).as('GETrates');
                cy.intercept('PUT', '**/prices', {
                    status: 204,
                    response: {},
                }).as('PUTprices');
                cy.visit(
                    `/events/${event.id}/prices`
                );
            });
        });
        it("can't create a new rate named exactly the same as an existing one", () => {
            cy.wait('@GETrates').then(xhr => {
                cy.get(elements.RATES_LIST)
                    .should('be.visible');
                cy.get(elements.NEW_RATE_INPUT)
                    .type(`${xhr.response.body[0].name}{enter}`)
                    .blur();
                cy.get(elements.NEW_RATE_INPUT)
                    .should('have.css', 'caret-color', 'rgb(255, 82, 9)')
                    .parents('mat-form-field')
                    .should('have.class', 'ng-invalid');
            })
        });
        it("can't update a rate named exactly the same as an existing one", () => {
            cy.wait('@GETrates').then(xhr => {
                cy.get(elements.RATES_LIST)
                    .should('be.visible');
                cy.get(elements.RATE_INPUT)
                    .first()
                    .clear()
                    .type(`${xhr.response.body[1].name}`)
                    .blur();
                cy.get(dialogs.WARN_DIALOG)
                    .should('be.visible');
                cy.get(dialogs.WARN_BUTTON_CONFIRM)
                    .click();
                cy.get(elements.RATE_INPUT)
                    .first()
                    .should('have.class', 'ng-invalid')
                    .should('have.css', 'border', '1px solid rgb(255, 82, 9)');
            })
        });
    })
})
