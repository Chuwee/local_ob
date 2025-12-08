import elements from './event-channels-components';
import $dialogs from '../../../shared/components/dialogs-components';
import $tabMenu from '../../../shared/components/tab-menu-components';
import $emptyList from '../../../shared/components/empty-list-components';

describe('Tests of Event/channel general data in special events with user operAdminCy', () => {
    let eventWithoutChan,
        eventSecondMkt,
        channel;
    before(() => {
        cy.fixture('events').then(events => {
            eventWithoutChan = events.eventWithoutChannels;
            eventSecondMkt = events.ecvMainEvent;
        });
        cy.fixture('channels').then(channels => {
            channel = channels.ecvMultiPortal;
        });
    });
    context('using eventWithoutChannels', () => {
        beforeEach(() => {
            cy.login('operAdminCy');
            cy.intercept(`**/events/${eventWithoutChan.id}/channels?**`).as(
                'GETevent/channels'
            );
            cy.visit(`/events/${eventWithoutChan.id}/channels`);
        });
        it('shows an empty list with a button to create a channel when there are no channels in the event', () => {
            cy.wait('@translations').then((xhr) => {
                const body = xhr.response.body;
                const empty = body.EVENTS.CHANNEL.EMPTY_LIST_MESSAGE;
                cy.contains('h2.title', empty).should('be.visible');
                cy.get($emptyList.EMPTY_LIST_CREATE_BUTTON).click();
                cy.get($dialogs.DIALOG).should('be.visible');
                const title = body.TITLES.SALES_SELECT_CHANNELS;
                cy.get($dialogs.DIALOG_TITLE).should('contain', title);
            });
        });
    });
    context('using eventSecondMkt and channel ecvMultiPortal', () => {
        beforeEach(() => {
            cy.login('operAdminCy');
            cy.visit(`/events/${eventSecondMkt.id}/channels/${channel.id}/general-data`);
        });
        it('enables the secondary market operative, navigates, sees the richUnsavedChanges modal and saves changes', () => {
            cy.get(elements.SEC_MKT_TOGGLE).click();
            cy.get($tabMenu.SELECTABLE_MENU_TAB).first().click();
            cy.getLiteral($dialogs.UNSAVED_CHANGES_MESSAGE_KEY).then(literal => {
                cy.get($dialogs.DIALOG_TEXT).should('have.text', literal.replace('<br>', ''));
            })
            cy.get($dialogs.DIALOG_BUTTON_CONFIRM).click();

            cy.get($dialogs.SNACKBAR_SUCCESS).should('be.visible');
            cy.url().should('not.contain', `/events/${eventSecondMkt.id}/channels/${channel.id}/general-data`);
        })
    });
});
