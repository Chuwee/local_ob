import elements from './event-sessions-planning-components';
import $tabMenu from '../../../shared/components/tab-menu-components';
import $dialogs from '../../../shared/components/dialogs-components';


describe('Tests of Event/Sessions/Planning', () => {
    context('using user entityMgrECVCy and event ecvMainEvent', () => {
        let event;
        before(() => {
            cy.fixture('events').then(events => { event = events.ecvMainEvent });
        });
        beforeEach(() => {
            cy.intercept('PUT', '**/sessions/*', {}).as('PUTsession');
            cy.intercept('GET', /\/sessions\/\d+$/).as('GETsession');
            cy.login('entityMgrECVCy');
        });
        it('edits name of the session, sees the richUnsavedChanges modal and saves changes', () => {
            cy.visit(`/events/${event.id}/sessions`);
            cy.url().should('contain', '/planning');
            cy.get(elements.SESSION_NAME_INPUT).clear().type('Edited');
            cy.wait('@GETsession').then(xhr => {
                const sessionId = xhr.response.body.id;
                cy.get($tabMenu.SELECTABLE_MENU_TAB).first().click();
                cy.getLiteral($dialogs.UNSAVED_CHANGES_MESSAGE_KEY).then(literal => {
                    cy.get($dialogs.DIALOG_TEXT).should('have.text', literal.replace('<br>', ''));
                })
                cy.get($dialogs.DIALOG_BUTTON_CONFIRM).click();
                cy.wait('@PUTsession').then(xhr => {
                    expect(xhr.request.body).to.have.property('name', 'Edited');
                });
                cy.get($dialogs.SNACKBAR_SUCCESS).should('be.visible');
                cy.url().should('not.contain', `/events/${event.id}/sessions/${sessionId}/planning`);
            })
        });
    })
})
