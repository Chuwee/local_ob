/* eslint-disable cypress/unsafe-to-chain-command */
import elements from './voucher-group-components';
import $bottombar from '../../../shared/components/bottombar-components';
import $dialogs from '../../../shared/components/dialogs-components';


describe('Tests of Channels/Voucher-group/Config/Redeem', () => {
    context('using user operAdminCy', () => {
        beforeEach(() => {
            cy.login('operAdminCy');
        });
        xit('sees error when choosing a redeem channel that already redeems another external voucher-group', () => {
            cy.intercept('PUT', '**/voucher-groups/*').as('PUTVoucherGroup');
            cy.intercept('GET', '**/channels?*').as('GETChannels');
            cy.visit('admin/vouchers/4516/group/configuration/redeem');
            cy.get('mat-spinner').should('be.visible');
            cy.wait('@GETChannels');
            cy.get('mat-spinner').should('not.to.exist');
            cy.get(elements.CONFIG.REDEEM_CHANNEL_RADIO)
                .first()
                .should('not.have.class', 'mat-mdc-radio-checked');
            cy.get(elements.CONFIG.REDEEM_CHANNEL_RADIO)
                .first()
                .click({ force: true })
                .should('have.class', 'mat-mdc-radio-checked');
            cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED).click();
            cy.wait('@PUTVoucherGroup').then(xhr => {
                expect(xhr.request.body).to.eql(
                    { channels: { scope: 'ALL', ids: [] } }
                );
                expect(xhr.response.statusCode).to.eql(409)
            });
            cy.getLiteral('API_ERRORS.ONLY_ONE_AVET_VOUCHER_PER_CHANNEL')
                .then(literal => {
                    cy.get($dialogs.DIALOG_TEXT).should('contain', literal)
                })
        });
    });
});
