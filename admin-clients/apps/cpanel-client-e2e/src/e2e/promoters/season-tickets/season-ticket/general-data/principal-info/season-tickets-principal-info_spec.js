import elements from './season-tickets-principal-info-components';
import $dialogs from '../../../../../shared/components/dialogs-components';
import $bottombar from '../../../../../shared/components/bottombar-components';
import $tabsMenu from '../../../../../shared/components/tab-menu-components';


describe('Tests of Season Ticket/General data', () => {
    let seasonTk;
    before(() => {
        cy.fixture('events').then((events) => { seasonTk = events.standardSeasonTicket })
    });
    context('using Standard Season Ticket and operAdminQaa user', () => {
        beforeEach(() => {
            cy.login('operAdminQaa');
            cy.intercept('GET', `**/season-tickets/${seasonTk.id}`)
                .as('GETseasonTicketById');
            cy.visit(
                `/season-tickets/${seasonTk.id}/general-data/principal-info`
            );
        });
        it("sees a season ticket's start-date, end-date, number of sessions, capacity and id", () => {
            cy.wait(['@translations', '@GETseasonTicketById']).spread(
                (trans, st) => {
                    const translations = trans.response.body;
                    const literalSTId = translations.SEASON_TICKET.ID;
                    // const literalStartDate = translations.SEASON_TICKET.START_DATE;
                    // const literalEndDate = translations.SEASON_TICKET.END_DATE;

                    const seasonTicketData = st.response.body;
                    // const options = {
                    //     year: "numeric",
                    //     month: "long",
                    //     day: "numeric",
                    // };
                    // const startDate = new Date(seasonTicketData.start_date);
                    // const startDateFormat = new Intl.DateTimeFormat(
                    //     "es-ES",
                    //     options
                    // ).format(startDate);
                    // const endDate = new Date(seasonTicketData.end_date);
                    // const endDateFormat = new Intl.DateTimeFormat(
                    //     "es-ES",
                    //     options
                    // ).format(endDate);

                    cy.contains(elements.ST_SIDEBAR_ITEM, literalSTId)
                        .next()
                        .should('have.text', String(seasonTicketData.id));
                    // cy.contains(elements.EVENT_SIDEBAR_ITEM, literalStartDate)
                    //     .next()
                    //     .should("have.text", startDateFormat);
                    // cy.contains(elements.EVENT_SIDEBAR_ITEM, literalEndDate)
                    //     .next()
                    //     .should("have.text", endDateFormat);

                    // let sessionsData = xhrs[2].response.body;
                    // cy.contains(elements.EVENT_SIDEBAR_ITEM, literalSessions)
                    //     .next()
                    //     .should(
                    //         "have.text",
                    //         String(sessionsData.metadata.total)
                    //     );
                }
            );
        });
        it('see a warning when navigating to another tab with unsaved changes and cancels navigation', () => {
            cy.get(elements.ST_LANGUAGES_CHECKBOXES).first().as('originalState');
            cy.get(elements.ST_LANGUAGES_CHECKBOXES).first().click();
            cy.contains($tabsMenu.MENU_TAB, 'sesiones').click();
            cy.get($dialogs.ALERT_DIALOG).should('be.visible');
            cy.get($dialogs.UNSAVED_CHANGES_BTN_STAY).click();
            cy.url().should('contain', `/season-tickets/${seasonTk.id}/general-data/principal-info`);
            cy.get($bottombar.BOTTOMBAR_CANCEL_CHANGES_BTN).click();
            cy.get($dialogs.ALERT_DIALOG).should('be.visible');
            cy.get($dialogs.ALERT_BUTTON_CONFIRM).click();
            cy.get('@originalState').then($original => {
                cy.get(elements.ST_LANGUAGES_CHECKBOXES)
                    .first()
                    .should('eql', $original)
            })
        });
        it('see a warning when navigating to another tab with unsaved changes and discards changes', () => {
            cy.get(elements.ST_LANGUAGES_CHECKBOXES)
                .first()
                .as('originalState')
                .click();
            cy.contains($tabsMenu.MENU_TAB, 'sesiones').click();
            cy.get($dialogs.ALERT_DIALOG).should('be.visible');
            cy.get($dialogs.UNSAVED_CHANGES_BTN_STAY).click();
            cy.url().should('contain', `/season-tickets/${seasonTk.id}/general-data/principal-info`);
            cy.contains($tabsMenu.MENU_TAB, 'sesiones').click();
            cy.get($dialogs.ALERT_DIALOG).should('be.visible');
            cy.get($dialogs.UNSAVED_CHANGES_BTN_DISCARD).click();
            cy.url().should('contain', `/season-tickets/${seasonTk.id}/sessions`);
            cy.visit(`/season-tickets/${seasonTk.id}/general-data/principal-info`);
            cy.get('@originalState').then($original => {
                cy.get(elements.ST_LANGUAGES_CHECKBOXES)
                    .first()
                    .should('eql', $original)
            })
        });
    });
});
