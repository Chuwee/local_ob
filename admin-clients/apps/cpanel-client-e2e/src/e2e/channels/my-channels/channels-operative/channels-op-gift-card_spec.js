import elements from './channels-operative_components';
import $bottombar from '../../../shared/components/bottombar-components';

describe('Tests of Channels/Operative/Gift-card tab', () => {
    let channel,
        channelWithoutGiftCard;
    before(() => {
        cy.fixture('channels').then((channels) => {
            channelWithoutGiftCard = channels.prtActivityMulti;
            channel = channels.channelMonoPortal;
        });
    });
    context('using user operAdminQaa', () => {
        beforeEach(() => {
            cy.login('operAdminQaa');
        });
        it('sees Gift Card configuration disabled in entities without gift cards', () => {
            cy.visit(`/channels/${channelWithoutGiftCard.id}/operative/gift-card`);
            cy.get(elements.GIFT_CARD.ENABLE_CHECKBOX)
                .should('have.class', 'mat-mdc-checkbox-disabled')
        });
    });
    context('using user channelMgrChannelCy', () => {
        beforeEach(() => {
            cy.login('channelMgrChannelCy');
        });
        it('sees Gift Card configuration enabled and sets a gift card', () => {
            cy.intercept('PUT', `**/channels/${channel.id}/vouchers`, {})
                .as('PUTvouchers');
            cy.visit(`/channels/${channel.id}/operative/gift-card`);
            cy.get(elements.GIFT_CARD.ENABLE_CHECKBOX)
                .should('not.have.class', 'mat-mdc-checkbox-disabled')
                .click()
                .should('have.class', 'mat-mdc-checkbox-checked');
            cy.get(elements.GIFT_CARD.GC_SELECT)
                .click();
            cy.get(elements.GIFT_CARD.GC_OPTION)
                .first()
                .click();
            cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED)
                .click();
            cy.wait('@PUTvouchers').then(xhr => {
                expect(xhr.request.body).eql(
                    {
                        gift_card:
                        {
                            enable: true,
                            id: 2106
                        }
                    }
                )
            });
        });
    });
});
