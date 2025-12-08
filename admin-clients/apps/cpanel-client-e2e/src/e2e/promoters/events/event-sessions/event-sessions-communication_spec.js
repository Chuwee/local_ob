import elements from './event-sessions-communication-components';
import $sessionEl from './event-sessions-components';
import $tabMenu from '../../../shared/components/tab-menu-components'


describe('Tests of Event/Sessions/Communication', () => {
    context('using user entityMgrECVCy and event ecvMainEvent', () => {
        let event;
        before(() => {
            cy.fixture('events').then(events => { event = events.ecvMainEvent });
        });
        beforeEach(() => {
            cy.login('entityMgrECVCy');
            cy.visit(`/events/${event.id}/sessions/${event.sessions.first.id}/communication`);
        });
        it('sees not the passbook download template button in session bulk edition mode', () => {
            cy.get($sessionEl.SESSION_LIST_ITEM_SELECTED, { timeout: 10000 })
                .should('have.length', 1);
            cy.get($sessionEl.SESSION_LIST_ITEM_CHECKBOX)
                .eq(1)
                .click();
            cy.get($sessionEl.SESSION_LIST_ITEM_SELECTED)
                .should('have.length', 2);
            cy.url().should('contain', '/sessions/multi/planning');
            cy.get(`${$tabMenu.MENU_TAB}[href*="/events/${event.id}/sessions/multi/communication"`)
                .click();
            cy.get(elements.PASSBOOK_DOWNLOAD_TMPL).should('not.be.visible');
        })
    })
})
