import elements from './event-gen-data-components';


describe('Tests of Event/General Data/Principal Info', () => {
    let event;
    context('with user eventMgrEventCy', () => {
        before(() => {
            cy.fixture('events').then((events) => {
                event = events.eventSimpleEvent;
            });
        });
        beforeEach(() => {
            cy.login('eventMgrEventCy');
            cy.intercept('GET', `**/events/${event.id}`).as('GETevent');
            cy.intercept('GET', `**/events/${event.id}/sessions?*`).as(
                'GETevent/sessions'
            );
            cy.intercept('PUT', `**/events/${event.id}`, {
                status: 204,
                response: {},
            }).as('PUTevent');
            cy.visit(
                `/events/${event.id}/general-data/principal-info`
            );
        });
        it("sees an event's start-date, end-date, number of sessions, capacity and event id", () => {
            cy.wait(['@translations', '@GETevent', '@GETevent/sessions']).then(
                (xhrs) => {
                    const translations = xhrs[0].response.body;
                    const literalEventId = translations.EVENTS.ID;
                    const literalSessions = translations.EVENTS.SESSIONS.TITLE;
                    const literalStartDate = translations.EVENTS.START_DATE;
                    const literalEndDate = translations.EVENTS.END_DATE;

                    const eventData = xhrs[1].response.body;
                    const options = {
                        year: 'numeric',
                        month: 'long',
                        day: 'numeric',
                    };
                    const startDate = new Date(eventData.start_date);
                    const startDateFormat = new Intl.DateTimeFormat(
                        'es-ES',
                        options
                    ).format(startDate);
                    const endDate = new Date(eventData.end_date);
                    const endDateFormat = new Intl.DateTimeFormat(
                        'es-ES',
                        options
                    ).format(endDate);

                    cy.contains(elements.EVENT_SIDEBAR_ITEM, literalEventId)
                        .next()
                        .should('have.text', String(eventData.id));
                    cy.contains(elements.EVENT_SIDEBAR_ITEM, literalStartDate)
                        .next()
                        .should('have.text', startDateFormat);
                    cy.contains(elements.EVENT_SIDEBAR_ITEM, literalEndDate)
                        .next()
                        .should('have.text', endDateFormat);

                    let sessionsData = xhrs[2].response.body;
                    cy.contains(elements.EVENT_SIDEBAR_ITEM, literalSessions)
                        .next()
                        .should(
                            'have.text',
                            String(sessionsData.metadata.total)
                        );
                }
            );
        });
        it('imports contact data from entity', () => {
            cy.get(elements.CONTACT_DATA_NAME).clear().should('have.value', '');
            cy.get(elements.CONTACT_DATA_SURNAME)
                .clear()
                .should('have.value', '');
            cy.get(elements.CONTACT_DATA_EMAIL)
                .clear()
                .should('have.value', '');
            cy.get(elements.CONTACT_DATA_PHONE)
                .clear()
                .should('have.value', '');
            cy.get(elements.CONTACT_DATA_IMPORT_BTN).click();
            cy.get('@myself').then((xhr) => {
                cy.log(xhr.response.body.username);
                cy.get(elements.CONTACT_DATA_NAME).should(
                    'have.value',
                    xhr.response.body.name
                );
                cy.get(elements.CONTACT_DATA_SURNAME).should(
                    'have.value',
                    xhr.response.body.last_name
                );
                cy.get(elements.CONTACT_DATA_EMAIL).should(
                    'have.value',
                    xhr.response.body.username
                );
                cy.get(elements.CONTACT_DATA_PHONE).should(
                    'have.value',
                    xhr.response.body.contact.primary_phone
                );
            });
        });
    });
    context('with user operAdminCy', () => {
        let multiEvent;
        before(() => {
            cy.fixture('events').then((events) => {
                multiEvent = events.ecvMainEvent;
            });
        });
        beforeEach(() => {
            cy.login('operAdminCy');
            cy.intercept('GET', `**/events/${multiEvent.id}`).as('GETmultiEvent');
            cy.intercept('GET', `**/events/${event.id}`).as('GETsimpleEvent');
        });
        it('sees invoice data editable in multiproducerEvent', () => {
            cy.visit(
                `/events/${multiEvent.id}/general-data/principal-info`
            );
            cy.wait(['@translations', '@GETmultiEvent']).then((xhrs) => {
                const translations = xhrs[0].response.body;
                const useProducerFiscalData =
                    xhrs[1].response.body.settings.use_producer_fiscal_data;
                cy.contains(
                    'mat-expansion-panel',
                    translations.EVENTS.INVOICE_DATA
                )
                    .find(elements.INVOICE_DATA_RADIO_BTN)
                    .should('have.length', 2);
                if (useProducerFiscalData) {
                    cy.contains(
                        elements.INVOICE_DATA_RADIO_BTN,
                        translations.EVENTS.PRODUCER_DATA
                    ).should('have.class', 'mat-mdc-radio-checked');
                    cy.get(elements.INVOICE_INFO_ITEM)
                        .eq(1)
                        .should($socialReason => {
                            let text = $socialReason.text().trim();
                            expect(text).to.eql(`${translations.EVENTS.SOCIAL_REASON}: ${xhrs[1].response.body.entity.name}`);
                        });
                }
            });
        });
        it('sees invoice data not editable in simpleEvent', () => {
            cy.visit(
                `/events/${event.id}/general-data/principal-info`
            );
            cy.wait('@translations').then((xhr) => {
                const translations = xhr.response.body;
                cy.contains(
                    'mat-expansion-panel',
                    translations.EVENTS.INVOICE_DATA
                )
                    .find(elements.INVOICE_DATA_RADIO_BTN)
                    .should('not.exist');
            });
        });
        it('sees custom categories when available', () => {
            cy.visit(
                `/events/${multiEvent.id}/general-data/principal-info`
            );
            cy.wait('@GETmultiEvent').then((xhr) => {
                cy.get(elements.CATEGORIES_CUSTOM_SELECT).should(
                    'have.text',
                    xhr.response.body.settings.categories.custom.description
                );
            });
        });
        it('sees only the categories select available', () => {
            cy.visit(
                `/events/${event.id}/general-data/principal-info`
            );
            cy.wait('@GETsimpleEvent').then((xhr) => {
                cy.get(elements.CATEGORIES_BASE_SELECT).should(
                    'have.text',
                    xhr.response.body.settings.categories.base.description
                );
                cy.get(elements.CATEGORIES_CUSTOM_SELECT).should('not.exist');
            });
        });
    });
});
