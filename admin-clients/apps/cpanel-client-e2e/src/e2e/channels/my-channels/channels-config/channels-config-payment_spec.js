import elements from './channels-config_components';
import $sharedElements from '../channel-shared_components';
import $bottombar from '../../../shared/components/bottombar-components';
import $dialogs from '../../../shared/components/dialogs-components';

describe('Tests of Channels/Configuration/Payment methods tab', () => {
    let channel,
        channelMultiLang;
    before(() => {
        cy.fixture('channels').then((channels) => {
            channel = channels.channelMultiPortal;
            channelMultiLang = channels.channelMonoPortal;
        });
    });
    context('using user operAdminCy', () => {
        beforeEach(() => {
            cy.login('operAdminCy');
        });
        it('sees Payment Methods configuration with operator user', () => {
            cy.visit(`/channels/${channel.id}/general-data`);
            cy.get($sharedElements.MENU_TAB_CONFIG).should('be.visible');
        });
        // needs updating to new component
        xit('creates a new gateway in a single language channel', () => {
            cy.visit(`/channels/${channel.id}/configuration/payment-methods`);
            cy.intercept('POST', `**/channels/${channel.id}/gateways/cash/configurations`, { configSid: '20211213174815' })
                .as('POSTgateway');
            cy.get(elements.PAY.NEW_GATEWAY_BTN)
                .click();
            cy.get(elements.PAY.NEW_DIALOG.NAME)
                .type('Cash');
            cy.get(elements.PAY.NEW_DIALOG.NAME_TRANSLATION)
                .type('Efectivo');
            cy.get(elements.PAY.NEW_DIALOG.GATEWAY_SELECTOR)
                .click();
            cy.get(elements.PAY.NEW_DIALOG.GATEWAY_OPTIONS.CASH)
                .click();
            cy.get(elements.PAY.NEW_DIALOG.CREATE_BTN)
                .click();
            cy.wait('@POSTgateway').then(xhr => {
                expect(xhr.request.body).eql(
                    {
                        'name': 'Cash',
                        'description': null,
                        'translations': {
                            name: { 'es-ES': 'Efectivo' },
                            subtitle: { 'es-ES': null }
                        },
                        'send_additional_data': false,
                        'price_range': { min: null, max: null },
                        'price_range_enabled': false,
                        'live': false,
                        'refund': false,
                        'attempts': null,
                        'field_values': {}
                    }
                );
            });
            cy.get($dialogs.SNACKBAR).should('be.visible');
        });
        // needs updating to new component
        xit('creates a new gateway in a multi language channel', () => {
            cy.visit(`/channels/${channelMultiLang.id}/configuration/payment-methods`);
            cy.intercept('POST', `**/channels/${channelMultiLang.id}/gateways/cash/configurations`, { configSid: '20211213174815' })
                .as('POSTgateway');
            cy.get(elements.PAY.NEW_GATEWAY_BTN)
                .click();
            cy.get(elements.PAY.NEW_DIALOG.GATEWAY_SELECTOR)
                .click();
            cy.get(elements.PAY.NEW_DIALOG.GATEWAY_OPTIONS.CASH)
                .click();
            cy.get(elements.PAY.NEW_DIALOG.NAME)
                .type('Cash');
            cy.get(elements.PAY.NEW_DIALOG.NAME_TRANSLATION)
                .type('Efectivo{enter}');
            cy.get(elements.PAY.NEW_DIALOG.NAME_TRANSLATION)
                .should('have.class', 'ng-invalid')
                .type('Efectiu');
            cy.get(elements.PAY.NEW_DIALOG.LANG_SELECTED)
                .next('mat-button-toggle')
                .click();
            cy.get(elements.PAY.NEW_DIALOG.LANG_SELECTED)
                .invoke('text')
                .then(lang => {
                    cy.get(elements.PAY.NEW_DIALOG.NAME_TRANSLATION_LABEL)
                        .invoke('text')
                        .should(label => {
                            expect(label).to.contain(lang);
                        });
                });
            cy.get(elements.PAY.NEW_DIALOG.NAME_TRANSLATION)
                .should('have.class', 'ng-invalid')
                .type('Cash');
            cy.get(elements.PAY.NEW_DIALOG.CREATE_BTN)
                .click();
            cy.wait('@POSTgateway').then(xhr => {
                expect(xhr.request.body).eql(
                    {
                        'name': 'Cash',
                        'description': null,
                        'translations': {
                            name: { 'es-ES': 'Efectivo', 'ca-ES': 'Efectiu', 'en-US': 'Cash' },
                            subtitle: { 'es-ES': null, 'ca-ES': null, 'en-US': null }
                        },
                        'send_additional_data': false,
                        'price_range': { min: null, max: null },
                        'price_range_enabled': false,
                        'live': false,
                        'refund': false,
                        'attempts': null,
                        'field_values': {}
                    }
                );
            });
            cy.get($dialogs.SNACKBAR).should('be.visible');
        });
        it('enables/disables voucher redeem and refunds in vouchers', () => {
            cy.intercept('GET', `**/channels/${channel.id}/vouchers`).as('GETvouchers');
            cy.visit(`/channels/${channel.id}/configuration/payment-methods`);
            cy.wait('@GETvouchers').then(xhr => {
                const isRedeemEnabled = xhr.response.body.allow_redeem_vouchers;
                const isRefundEnabled = xhr.response.body.allow_refund_to_vouchers;
                cy.intercept('PUT', `**/channels/${channel.id}/vouchers`, {}).as('PUTvouchers');
                cy.get(elements.PAY.VOUCHERS_REDEEM_CHECKBOX)
                    .click();
                cy.get(elements.PAY.VOUCHERS_REFUND_CHECKBOX)
                    .click();
                cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED)
                    .click();
                cy.wait('@PUTvouchers').then(xhr => {
                    expect(xhr.request.body).eql(
                        {
                            allow_redeem_vouchers: !isRedeemEnabled,
                            allow_refund_to_vouchers: !isRefundEnabled
                        }
                    );
                });
                cy.get($dialogs.SNACKBAR).should('be.visible');
            })
        })
    });
    context('using user channelMgrChannelCy', () => {
        beforeEach(() => {
            cy.login('channelMgrChannelCy');
        });
        it('sees not Payment Methods configuration with entity user', () => {
            cy.visit(`/channels/${channel.id}/general-data`);
            cy.get($sharedElements.MENU_TAB_CONFIG).should('not.exist');
        });
    });
})
