import elements from './voucher-components';
import $dialogs from '../../../../../shared/components/dialogs-components';


describe('Tests of Vouchers/Voucher-codes/voucher', () => {
    context('with user channelMgrChannelCy in ChannelEntityManualCodePin', () => {
        let voucher;
        before(() => {
            cy.fixture('channels/vouchers').then((vouchers) => {
                voucher = vouchers.ChannelEntityManualCodePin;
            });
        });
        beforeEach(() => {
            cy.login('channelMgrChannelCy');
            cy.intercept(
                'POST',
                `**/voucher-groups/${voucher.voucherGroupId}/vouchers/${voucher.voucherUnlimited50}/send-email`,
                { status: 204, response: {} }
            ).as('POSTsendEmail');
            cy.visit(
                `/vouchers/${voucher.voucherGroupId}/voucher-codes/${voucher.voucherUnlimited50}`
            );
        });
        it('send email with manual code', () => {
            cy.get(elements.SEND_EMAIL_BTN, { timeout: 10000 }).click();
            cy.get(elements.EMAIL.EMAIL_ADDRESS_INPUT).type(
                'mpuertas@oneboxtds.com'
            );
            cy.get(elements.EMAIL.LANG_INPUT).click();
            cy.contains(elements.EMAIL.LANG_OPTION, 'Català').click();
            cy.get(elements.EMAIL.SUBJECT_INPUT).type('Asunto');
            cy.get(elements.EMAIL.BODY_TEXT_AREA).type('Cuerpo');
            cy.get(elements.EMAIL.SEND_BTN).click();
            cy.wait('@POSTsendEmail').then((xhr) => {
                expect(xhr.request.body).to.eql({
                    type: 'BASIC',
                    language: 'ca-ES',
                    email: 'mpuertas@oneboxtds.com',
                    subject: 'Asunto',
                    body: 'Cuerpo',
                });
                cy.get($dialogs.SNACKBAR_SUCCESS).should('be.visible');
            });
        });
    }
    );
});
