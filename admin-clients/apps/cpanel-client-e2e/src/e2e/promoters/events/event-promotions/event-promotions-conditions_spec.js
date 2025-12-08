import elements from './event-promotions-conditions-components';
import $bottombar from '../../../shared/components/bottombar-components';
// import $dialogs from '../../../shared/components/dialogs-components';


describe('Tests of Event / Promotions / Conditions panel', () => {
    context('with user entityMgrECVCy in promotionsEvent', () => {
        let event;
        before(() => {
            cy.fixture('events').then(eventsData => {
                event = eventsData.promotionsEvent;
            });
        });
        beforeEach(() => {
            cy.login('entityMgrECVCy');
            cy.intercept('PUT', '**/events/*/promotions/*', {
                status: 204,
                response: {}
            }).as('PUTpromoTmpl');
        });
        it('edits the conditions of an event promotion', () => {
            cy.intercept('GET', '**/mgmt-api/v1/events/*/promotions/*').as('GETeventPromoConditions');
            cy.visit(`/events/${event.id}/promotions/${event.promotions.plusCollectiveEventLimit.id}/conditions`);
            cy.wait('@GETeventPromoConditions', { requestTimeout: 10000 }).then(originalPromo => {
                const surchargesChannelEnabled = originalPromo.response.body.surcharges.channel_fees;
                const acRestricted = originalPromo.response.body.access_control_restricted;
                cy.get(elements.ENABLE_ALT_SURCHARGES_CHANNEL).click();
                cy.get(elements.ACCESS_CONTROL_RESTRICTED).click();
                cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED).click();
                cy.wait('@PUTpromoTmpl').then(xhr => {
                    expect(xhr.request.body).to.nested.include(
                        { 'surcharges.channel_fees': !surchargesChannelEnabled }
                    );
                    expect(xhr.request.body).to.nested.include(
                        { 'access_control_restricted': !acRestricted }
                    );
                })
            })
        });
    });
})
