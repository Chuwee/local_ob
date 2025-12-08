import elements from './event-prices-components';
import $bottombar from '../../../shared/components/bottombar-components';
import $dialogs from '../../../shared/components/dialogs-components';
import $tabsMenu from '../../../shared/components/tab-menu-components'


describe('Tests of Event/Prices Prices panel', () => {
    context('with user eventMgrEventCy in pricesEvent', () => {
        let event;
        before(() => {
            cy.fixture('events').then((events) => {
                event = events.pricesEvent;
            });
        });
        beforeEach(() => {
            cy.login('eventMgrEventCy');
            cy.intercept('GET', `**/events/${event.id}`).as('GETevent');
            cy.intercept('GET', '**/venue-templates?*').as(
                'GETvenue-templates'
            );
            cy.intercept('PUT', '**/prices', {
                status: 204,
                response: {}
            }).as('PUTprices');
            cy.intercept('GET', '**/price-types').as('GETpriceTypes');
            cy.visit(`/events/${event.id}/prices/price-types`);
            cy.wait('@GETpriceTypes', { requestTimeout: 10000 });
        });
        it('edits the prices of an event in Ready status', () => {
            cy.get(elements.PRICE_VALUE).first().click();
            cy.get(elements.PRICE_WRITE_VALUE)
                .first()
                .then(($price) => {
                    expect($price).to.have.css(
                        'caret-color',
                        'rgb(59, 160, 168)'
                    );
                    cy.get($price).type('9.90').blur();
                    cy.getUserLanguage().then((lang) => {
                        let locale =
                            lang === 'en-US' || lang === 'es-CR' ? 'en' : 'ca';
                        let priceValue = parseFloat('9.90').toLocaleString(
                            locale,
                            {
                                style: 'currency',
                                currency: 'EUR',
                                minimumFractionDigits: 2,
                            }
                        );
                        cy.get(elements.PRICE_VALUE)
                            .first()
                            .should('contain', priceValue);
                    });
                });
            cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED).click();
            cy.wait('@PUTprices').then((xhr) => {
                expect(xhr.request.body[0]).to.have.property('value', 9.9);
            });
        });
        it('can\'t put a negative price', () => {
            cy.get(elements.PRICE_VALUE).first().click();
            cy.get(elements.PRICE_WRITE_VALUE)
                .first()
                .then(($price) => {
                    expect($price).to.have.css(
                        'caret-color',
                        'rgb(59, 160, 168)'
                    );
                    cy.get($price).type('-9.90').blur();
                });
            cy.get(elements.PRICE_CELL)
                .first()
                .should('have.class', 'ng-invalid')
                .find('.mat-input-element')
                .should('have.css', 'caret-color', 'rgb(255, 82, 9)');
        });
        it('changes the venue template and edits a price', () => {
            cy.wait('@GETvenue-templates').then((xhrVenueTempl) => {
                const venueTemplates = xhrVenueTempl.response.body.data;
                cy.get('#venue-selector').click();
                cy.intercept(
                    `**/venue-templates/${venueTemplates[1].id}/prices`
                ).as('GETprices');
                cy.get('#venue-selector-panel')
                    .contains(`${venueTemplates[1].name}`)
                    .click();
                cy.wait('@GETprices').then((xhrPrices) => {
                    const firstPrice = xhrPrices.response.body[0];
                    cy.get(elements.PRICE_VALUE).first().click();
                    cy.get(elements.PRICE_WRITE_VALUE).first().type('1').blur();
                    cy.get(
                        $bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED
                    ).click();
                    cy.wait('@PUTprices').then((xhr) => {
                        const expectedPayload = [
                            {
                                price_type_id: firstPrice.price_type.id,
                                rate_id: firstPrice.rate.id,
                                ticket_type: firstPrice.ticket_type,
                                value: 1,
                            },
                        ];
                        expect(xhr.request.body).to.eql(expectedPayload);
                        cy.get($dialogs.SNACKBAR).should('be.visible');
                    });
                });
            });
        });
        it('sees a warning when navigating to another tab with unsaved changes and cancels navigation', () => {
            cy.get(elements.PRICE_VALUE)
                .first()
                .invoke('text')
                .as('originalPrice');
            cy.get(elements.PRICE_VALUE).first().click();
            cy.get(elements.PRICE_WRITE_VALUE).first().type(100).blur();
            cy.contains($tabsMenu.SELECTABLE_MENU_TAB, 'Sesiones').click();
            cy.get($dialogs.ALERT_DIALOG).should('be.visible');
            cy.get($dialogs.UNSAVED_CHANGES_BTN_STAY).click();
            cy.url().should('contain', `/events/${event.id}/prices`);
            cy.get($bottombar.BOTTOMBAR_CANCEL_CHANGES_BTN).click();
            cy.get($dialogs.ALERT_DIALOG).should('be.visible');
            cy.get($dialogs.ALERT_BUTTON_CONFIRM).click();
            cy.get('@originalPrice').then((op) => {
                cy.get(elements.PRICE_VALUE).first().should('contain', op);
            });
        });
        it('sees a warning when navigating to another tab with unsaved changes and discards the changes', () => {
            cy.get(elements.PRICE_VALUE)
                .first()
                .invoke('text')
                .as('originalPrice');
            cy.get(elements.PRICE_VALUE).first().click();
            cy.get(elements.PRICE_WRITE_VALUE).first().type(100).blur();
            cy.contains($tabsMenu.SELECTABLE_MENU_TAB, 'Sesiones').click();
            cy.get($dialogs.ALERT_DIALOG).should('be.visible');
            cy.get($dialogs.UNSAVED_CHANGES_BTN_DISCARD).click();
            cy.url().should('contain', `/events/${event.id}/sessions`);
            cy.visit(`/events/${event.id}/prices/price-types`);
            cy.get('@originalPrice').then((op) => {
                cy.get(elements.PRICE_VALUE).first().should('contain', op);
            });
        });
        it('sees a warning when navigating to another tab with unsaved changes and saves the changes', () => {
            cy.get(elements.PRICE_VALUE)
                .first()
                .invoke('text')
                .as('originalPrice');
            cy.get(elements.PRICE_VALUE).first().click();
            cy.get(elements.PRICE_WRITE_VALUE).first().type(100).blur();
            cy.contains($tabsMenu.SELECTABLE_MENU_TAB, 'Sesiones').click();
            cy.get($dialogs.ALERT_DIALOG).should('be.visible');
            cy.get($dialogs.UNSAVED_CHANGES_BTN_SAVE).click();
            cy.wait('@PUTprices');
            cy.url().should('contain', `/events/${event.id}/sessions`);
        });
    });
    context('with user eventMgrEventCy in activityEvent', () => {
        let event;
        before(() => {
            cy.fixture('events').then((events) => {
                event = events.activityEvent;
            });
        });
        beforeEach(() => {
            cy.login('eventMgrEventCy');
            cy.intercept('PUT', `**/events/${event.id}`, { statusCode: 204 }).as('PUTevent');
            cy.intercept('PUT', '**/prices', { statusCode: 204 }).as('PUTprices');
            cy.intercept('GET', '**/price-types').as('GETpriceTypes');
            cy.visit(`/events/${event.id}/prices/price-types`);
            cy.wait('@GETpriceTypes', { requestTimeout: 10000 });
        });
        it('edits the prices of group tickets in Activity event', () => {
            cy.getLiteral('EVENTS.PRICEZONE.TITLE_GROUP_CONFIGURATION_TABLE').then(literal => {
                cy.contains(elements.PRICES_MATRIX, literal, { timeout: 10000 }).within(table => {
                    cy.get(elements.PRICE_VALUE).first().click();
                    cy.get(elements.PRICE_WRITE_VALUE).first().should('be.visible').type(60).blur();
                })
                cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED).click();
                cy.wait('@PUTprices').then((xhr) => {
                    expect(xhr.request.body[0]).to.have.property('value', 60);
                });
            })
        });
        it('sets the price of group tickets for each person of the group', () => {
            cy.get(elements.PRICE_GROUP_POLICY).eq(1).click();
            cy.get(elements.PRICE_GROUP_COMPANIONS).click();
            cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED).click();
            cy.wait('@PUTevent').then((xhr) => {
                expect(xhr.request.body).to.eql(
                    {
                        settings: {
                            groups: {
                                price_policy: 'INDIVIDUAL',
                                companions_payment: true
                            }
                        }
                    }
                );
            });
        })
    });
});
