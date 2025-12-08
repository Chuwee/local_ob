import elements from './event-sessions-components';
import moment from 'moment-timezone';


describe('Tests of Event/Sessions create session', () => {
    context('using event pricesEvent of event entity and user operAdminCy', () => {
        let event;
        before(() => {
            cy.fixture('events').then(events => { event = events.pricesEvent });
        });
        beforeEach(() => {
            cy.login('operAdminCy');
            cy.intercept('POST', `**/events/${event.id}/sessions`, {
                status: 201,
                response: {
                    id: 268894,
                },
            }).as('POSTsessions');
            cy.intercept('GET', `**/events/${event.id}`).as('GETevent');
            cy.intercept('GET', '**/taxes').as('GETtaxes');
            cy.intercept('GET', '**/rates').as('GETrates');
            cy.visit(`/events/${event.id}/sessions`);
        });
        it('edits the default rate and changes visibility of rates when creating a new session', () => {
            cy.intercept('GET', `**/events/${event.id}/sessions?sort=start_date%3Adesc&limit=1&offset=0&type=SESSION`)
                .as('GETlastSession');
            cy.get(elements.NEW_SESSION_BUTTON, { timeout: 10000 }).click();
            cy.get(elements.NEW_SESSION_SINGLE).click();
            cy.wait('@GETlastSession').then(xhr => {
                cy.get(elements.NEW_SESSION_NAME).should('have.value', xhr.response.body.data[0].name)
                    .clear().type('Test Session');
            });
            let rate1, rate2, rate3;
            cy.wait('@GETrates').then(xhr => {
                rate1 = xhr.response.body[1];
                rate2 = xhr.response.body[2];
                rate3 = xhr.response.body[3];
                cy.get(elements.NEW_SESSION_RATES_ITEMS).should('have.length', xhr.response.body.length);
                cy.contains(elements.NEW_SESSION_RATE_ITEM, rate1.name, { matchCase: true })
                    .find(elements.RATE_DEFAULT_ICON).click();
                cy.contains(elements.NEW_SESSION_RATE_ITEM, rate1.name, { matchCase: true })
                    .find(elements.RATE_DEFAULT_ICON).should('have.attr', 'aria-pressed', 'true');
                cy.contains(elements.NEW_SESSION_RATE_ITEM, rate1.name, { matchCase: true })
                    .find(elements.RATE_VISIBLE_ICON)
                    .should('have.attr', 'aria-pressed', 'true');
                cy.contains(elements.NEW_SESSION_RATE_ITEM, rate3.name, { matchCase: true })
                    .find(elements.RATE_VISIBLE_ICON)
                    .click();
                cy.contains(elements.NEW_SESSION_RATE_ITEM, rate3.name, { matchCase: true })
                    .find(elements.RATE_VISIBLE_ICON)
                    .should('have.attr', 'aria-pressed', 'true');
                cy.contains(elements.NEW_SESSION_RATE_ITEM, rate2.name, { matchCase: true })
                    .find(elements.RATE_DEFAULT_ICON)
                    .should('have.attr', 'aria-pressed', 'false');
                cy.contains(elements.NEW_SESSION_RATE_ITEM, rate2.name, { matchCase: true })
                    .find(elements.RATE_VISIBLE_ICON)
                    .click();
                cy.contains(elements.NEW_SESSION_RATE_ITEM, rate2.name, { matchCase: true })
                    .find(elements.RATE_VISIBLE_ICON)
                    .should('have.attr', 'aria-pressed', 'false');
            });
            cy.wait('@GETevent').then(xhr => {
                cy.get(elements.NEW_SESSION_VENUE).click();
                cy.contains(elements.NEW_SESSION_SELECT_OPTION, xhr.response.body.venue_templates[0].name)
                    .click();
            });
            cy.wait('@GETtaxes').then(xhr => {
                cy.get(elements.NEW_SESSION_TIX_TAX).click();
                cy.contains(elements.NEW_SESSION_SELECT_OPTION, xhr.response.body[0].name)
                    .click();
                cy.get(elements.NEW_SESSION_FEE_TAX).click();
                cy.contains(elements.NEW_SESSION_SELECT_OPTION, xhr.response.body[0].name)
                    .click();
            })
            cy.get(elements.NEW_SESSION_START_DATE_INPUT)
                .invoke('val')
                .then(sessionStartDate => {
                    cy.get(elements.NEW_SESSION_BOOKING_END_DATE_INPUT)
                        .should('have.value', sessionStartDate);
                });
            cy.get(elements.NEW_SESSION_START_DATE_TIME_INPUT)
                .invoke('val')
                .then(sessionStartDateTime => {
                    cy.get(elements.NEW_SESSION_BOOKING_END_DATE_TIME_INPUT)
                        .should('have.value', sessionStartDateTime);
                });
            cy.get(elements.NEW_SESSION_BOOKING_END_DATE_MODIFIER_SELECTOR).click();
            cy.getElByKey('EVENTS.SESSION.DATES_MOD_OPTS.START', 'mat-option')
                .should('have.class', 'mdc-list-item--selected');
            cy.getLiteral('EVENTS.SESSION.DATES_MOD_OPTS.START_MINUS_HALF_HOUR')
                .then(literal => {
                    cy.contains('mat-option', literal)
                        .click();
                    cy.get(elements.NEW_SESSION_BOOKING_END_DATE_MODIFIER_SELECTOR)
                        .should('have.text', literal);
                });
            cy.get(elements.NEW_SESSION_CONFIRM_CREATE).click();
            cy.wait('@POSTsessions').then((xhr) => {
                expect(xhr.request.body).to.contain({
                    name: 'Test Session',
                });
                expect(xhr.request.body.rates).to.eql([
                    { id: rate1.id, name: rate1.name, default: true, visible: true },
                    { id: rate3.id, name: rate3.name, default: false, visible: true },
                ]);
            });
        });
    }
    );
    context('using activityEvent of event entity and user eventMgrEventCy', () => {
        let event;
        before(() => {
            cy.fixture('events').then((events) => { event = events.activityEvent });
        });
        beforeEach(() => {
            cy.login('eventMgrEventCy');
            cy.intercept('GET', `**/events/${event.id}`).as('GETevent');
            cy.intercept('GET', /\/sessions\/\d+$/gm).as('GETsession');
            cy.intercept('GET', '**/taxes').as('GETtaxes');
            cy.intercept('POST', `**/events/${event.id}/sessions`, {
                status: 201,
                response: { id: 268894 },
            }).as('POSTsessions');
            cy.visit(`/events/${event.id}/sessions`);
        });
        it('creates a new groups-only activity session', () => {
            // wait for session request to autocomplete the dates in creation modal
            cy.wait('@GETsession', { timeout: 10000 }).then((xhr) => {
                const releaseDate = xhr.response.body.settings.release.date;
                cy.get(elements.NEW_SESSION_BUTTON).click();
                cy.get(elements.NEW_SESSION_SINGLE).click();
                //wait until activitySaleType has one option checked
                cy.get('[formcontrolname="activitySaleType"] .mat-mdc-radio-checked');
                cy.get(elements.NEW_SESSION_ACT_SALE_GROUP)
                    .parents('mat-radio-button')
                    .click()
                    .should('have.class', 'mat-mdc-radio-checked');
                cy.get(elements.NEW_SESSION_DATE)
                    .invoke('val')
                    .then((startDate) => {
                        const start_date = moment
                            .tz(startDate, 'DD/MM/YYYY', 'Europe/Berlin')
                            .add(10, 'hours')
                            .format();
                        cy.wait('@GETevent').then(xhrEvent => {
                            cy.get(elements.NEW_SESSION_VENUE).click();
                            cy.contains(elements.NEW_SESSION_SELECT_OPTION, xhrEvent.response.body.venue_templates[0].name)
                                .click();
                        });
                        cy.wait('@GETtaxes').then(xhrTaxes => {
                            cy.get(elements.NEW_SESSION_TIX_TAX).click();
                            cy.contains(elements.NEW_SESSION_SELECT_OPTION, xhrTaxes.response.body[0].name)
                                .click();
                            cy.get(elements.NEW_SESSION_FEE_TAX).click();
                            cy.contains(elements.NEW_SESSION_SELECT_OPTION, xhrTaxes.response.body[0].name)
                                .click();
                        })
                        cy.get(elements.NEW_SESSION_CONFIRM_CREATE).click();
                        cy.wait('@POSTsessions').then((xhr) => {
                            expect(xhr.request.body).to.contain({
                                activity_sale_type: 'GROUP',
                            });
                            expect(xhr.request.body.dates).to.include({
                                bookings_end: start_date,
                                bookings_start: releaseDate,
                                channels: releaseDate,
                                sales_end: start_date,
                                sales_start: releaseDate,
                                start: start_date
                            });
                        });
                    });
            });
        });
        it('creates multiple activity sessions with relative dates and without duration', () => {
            cy.intercept('POST', `**/events/${event.id}/sessions/bulk`, {
                status: 201,
                response: { id: 268894 },
            }).as('POSTsessions');
            cy.wait('@GETsession', { timeout: 15000 }).then(() => {
                cy.get(elements.NEW_SESSION_BUTTON).click();
                cy.get(elements.NEW_SESSION_MULTI).click();
                cy.get(elements.NEW_SESSION_RELEASE_RELATIVE_RADIO).click();
                cy.get(elements.NEW_SESSION_BOOK_START_RELATIVE_RADIO).click();
                cy.get(elements.NEW_SESSION_BOOK_END_RELATIVE_RADIO).click();
                cy.get(elements.NEW_SESSION_PURCHASE_START_RELATIVE_RADIO).click();
                cy.get(elements.NEW_SESSION_PURCHASE_END_RELATIVE_RADIO).click();
                cy.get(elements.NEW_SESSION_VENUE).click();
                cy.get(elements.NEW_SESSION_SELECT_OPTION).first().click();
                cy.get(elements.NEW_SESSION_TIX_TAX).click();
                cy.get(elements.NEW_SESSION_SELECT_OPTION).first().click();
                cy.get(elements.NEW_SESSION_FEE_TAX).click();
                cy.get(elements.NEW_SESSION_SELECT_OPTION).first().click();
                cy.get(elements.NEW_SESSION_CONFIRM_CREATE).click();
                cy.get(elements.NEW_SESSION_REPEAT_PERIOD_START)
                    .invoke('val')
                    .then((startDate) => {
                        const relativeDate = moment
                            .tz(startDate, 'DD/MM/YYYY', 'Europe/Berlin')
                            .add(10, 'hours')
                            .subtract(1, 'minutes')
                            .format();
                        const releaseDate = moment
                            .tz(startDate, 'DD/MM/YYYY', 'Europe/Berlin')
                            .add(10, 'hours')
                            .format();
                        cy.get(elements.NEW_SESSION_REPEAT_PERIOD_END)
                            .click()
                        cy.contains('.mat-calendar-content td', parseInt(startDate.substring(0, 2)))
                            .click()//.type('{esc}')
                        cy.get(elements.NEW_SESSION_REPEAT_DAYS).first().click();
                        cy.get(elements.NEW_SESSION_CONFIRM_CREATE).click();
                        cy.get('app-sessions-calendar').should('be.visible');
                        cy.get(elements.NEW_SESSION_CONFIRM_CREATE).click();
                        cy.wait('@POSTsessions')
                            .then((xhr) => {
                                expect(xhr.request.body[0].dates).to.include({
                                    bookings_end: relativeDate,
                                    bookings_start: relativeDate,
                                    channels: relativeDate,
                                    sales_end: relativeDate,
                                    sales_start: relativeDate,
                                    start: releaseDate,
                                });
                            });
                    });
            })
        })
    }
    );
});
// # TO DO: Scenario: Create a session of an Avet event and check its restrictions
