import * as utils from '../shared/utils/utils';

describe('Tests of breadcrumbs using operAdminCy user', { tags: '@pgl' }, () => {
    const baseUrl = Cypress.config('baseUrl');
    const activeClass = 'mdc-tab--active';
    context('using ecvMainEvent', () => {
        let translations;
        let event;
        beforeEach(() => {
            cy.login('operAdminCy');
            cy.fixture('events').then((events) => { event = events.ecvMainEvent });
            cy.visit('/events');
            cy.wait('@translations').then((xhr) => {
                translations = xhr.response.body;
            });
        });
        it('shows breadcrumbs path when navigating to events tabs', () => {
            cy.url().should('contain', '/events?');
            const HOME = utils.getNestedValue(translations, 'TITLES.HOME');
            const MY_EVENTS = utils.getNestedValue(translations, 'TITLES.MY_EVENTS');
            cy.get('app-breadcrumbs').should(
                'contain.text',
                `${HOME} > ${MY_EVENTS}`
            );
            cy.visit(`/events/${event.id}`);
            cy.url().should('contain', `/events/${event.id}`);
            const GENERAL_DATA = utils.getNestedValue(translations, 'EVENTS.GENERAL_DATA');
            const PRINCIPAL_INFO = utils.getNestedValue(translations, 'EVENTS.PRINCIPAL_INFO');
            cy.get(`[href*="${baseUrl}/events/${event.id}/venue-templates"]`).click().should('have.class', activeClass);
            cy.url().should('contain', `/events/${event.id}/venue-templates/`);
            const VENUE_TEMPLATES = utils.getNestedValue(translations, 'EVENTS.VENUE_TEMPLATES');
            cy.get('.mat-expanded .template-name').then(tmplName => {
                const name = tmplName.text().trim();
                cy.get('app-breadcrumbs').should(
                    'contain.text',
                    `${HOME} > ${MY_EVENTS} > ${event.name} > ${VENUE_TEMPLATES} > ${name}`
                );
            });
            cy.get(`[href*="${baseUrl}/events/${event.id}/prices"]`).click().should('have.class', activeClass);
            cy.url().should('contain', `/events/${event.id}/prices`);
            const PRICES = utils.getNestedValue(translations, 'EVENTS.PRICES');
            cy.get('app-breadcrumbs').should(
                'contain.text',
                `${HOME} > ${MY_EVENTS} > ${event.name} > ${PRICES}`
            );
            cy.get(`[href*="${baseUrl}/events/${event.id}/sessions"]`).click().should('have.class', activeClass);
            cy.url().should('contain', `/events/${event.id}/sessions`);
            const SESSIONS = utils.getNestedValue(translations, 'EVENTS.SESSIONS.TITLE');
            const PLANNING = utils.getNestedValue(translations, 'EVENTS.PLANNING');
            cy.get('.session-item.selected .session-title').first().then($title => {
                const sessionsName = $title.text().trim();
                cy.get('app-breadcrumbs').should(
                    'contain.text',
                    `${HOME} > ${MY_EVENTS} > ${event.name} > ${SESSIONS} > ${sessionsName} > ${PLANNING}`
                );
            });
            cy.get(`[href*="${baseUrl}/events/${event.id}/communication"]`).click().should('have.class', activeClass);
            cy.url().should('contain', `/events/${event.id}/communication/channel-contents`);
            const COMM = utils.getNestedValue(translations, 'EVENTS.COMMUNICATION.TITLE');
            const COMM_CHAN = utils.getNestedValue(translations, 'EVENTS.COMMUNICATION.CHANNEL.TITLE');
            cy.get('app-breadcrumbs').should(
                'contain.text',
                `${HOME} > ${MY_EVENTS} > ${event.name} > ${COMM} > ${COMM_CHAN}`
            );
            cy.get(`[href*="${baseUrl}/events/${event.id}/channels"]`).click().should('have.class', activeClass);
            cy.url().should('contain', `/events/${event.id}/channels`);
            const CHANNELS = utils.getNestedValue(translations, 'EVENTS.CHANNELS');
            const CH_GEN_DATA = utils.getNestedValue(translations, 'CHANNELS.GENERAL_DATA');
            cy.get('mat-list-option[aria-selected="true"] .list-item-title').first().then($title => {
                const channelName = $title.text().trim();
                cy.get('app-breadcrumbs').should(
                    'contain.text',
                    `${HOME} > ${MY_EVENTS} > ${event.name} > ${CHANNELS} > ${channelName} > ${CH_GEN_DATA}`
                );
            });
            cy.get('.mat-mdc-tab-header-pagination-after').first().click();
            cy.get('[data-test="promotionsTab"]').click({ force: true }).should('have.class', activeClass);
            cy.url().should('contain', `/events/${event.id}/promotions`);
            const PROMOTIONS = utils.getNestedValue(translations, 'EVENTS.PROMOTIONS.TITLE');
            cy.get('app-breadcrumbs').should(
                'contain.text',
                `${HOME} > ${MY_EVENTS} > ${event.name} > ${PROMOTIONS}`
            );
        });
    });
});
