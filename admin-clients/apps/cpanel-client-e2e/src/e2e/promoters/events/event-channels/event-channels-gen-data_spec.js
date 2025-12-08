import elements from './event-channels-components';
import $dialogs from '../../../shared/components/dialogs-components';
import $bottombar from '../../../shared/components/bottombar-components';

describe('Tests of Event/channel general data in eventSimpleEvent with user operAdmin', () => {
    let event;
    before(() => {
        cy.fixture('events').then((events) => {
            event = events.eventSimpleEvent;
        });
    });
    beforeEach(() => {
        cy.login('operAdminCy');
        cy.intercept(`**/events/${event.id}/channels?**`).as(
            'GETevent/channels'
        );
        cy.visit(`/events/${event.id}/channels`);
    });
    context('using channelWithoutSalesReq', () => {
        let channel;
        beforeEach(() => {
            cy.getFromFixture('channels', 'channelWithoutSalesReq').then(
                (chan) => {
                    channel = chan;
                    cy.contains(elements.CHANNEL_LIST_SIDEBAR, channel.name).click();
                }
            );
        });
        it('creates a Sales Request from an event', () => {
            cy.intercept('POST', '**!/request-approval*', {}).as(
                'POSTrequest-approval'
            );
            cy.get(elements.CHANNEL_SALE_REQUEST_BUTTON)
                .should('not.have.class', 'mat-mdc-button-disabled')
                .click();
            cy.get($dialogs.ALERT_BUTTON_CONFIRM)
                .should('not.have.class', 'mat-mdc-button-disabled');
            //cy.wait('@POSTrequest-approval').then((xhr) => {
            //    expect(xhr.request.url).to.contain(
            //        `/channels/${channel.id}/request-approval`
            //    );
            //});
        });
        it('cancels creation of a Sales Request from an event', () => {
            cy.intercept('POST', '**!/request-approval*', {}).as(
                'POSTrequest-approval'
            );
            cy.get(elements.CHANNEL_SALE_REQUEST_BUTTON)
                .should('not.have.class', 'mat-button-disabled')
                .click();
            cy.get($dialogs.ALERT_BUTTON_CANCEL).click();
            cy.get(elements.CHANNEL_SALE_REQUEST_BUTTON).should(
                'not.have.class',
                'mat-button-disabled'
            );
        });
    });
    context('using channelMultiPortal', () => {
        let channel;
        beforeEach(() => {
            cy.getFromFixture('channels', 'channelMultiPortal').then(
                (chan) => {
                    channel = chan;
                    cy.contains(elements.CHANNEL_LIST_SIDEBAR, channel.name).click();
                    cy.intercept('PUT', `**/channels/${channel.id}`, { status: 204, response: {} })
                        .as('PUTchannel');
                }
            );
        });
        it('enables event operative for the channel', () => {
            // disables event operative in case someone fucked data
            const $toggle = Cypress.$(elements.USE_OPERATIVE_TOGGLE)
            if ($toggle.hasClass('mdc-switch--checked')) {
                cy.get(elements.USE_OPERATIVE_TOGGLE).click();
                cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED).click();
            }
            // test begins
            cy.get(elements.RELEASE_DATEPICKER).parents('mat-form-field')
                .should('not.have.class', 'mat-form-field-disabled');
            cy.get(elements.USE_OPERATIVE_TOGGLE).click()
                .should('not.be.checked');
            cy.get(elements.RELEASE_DATEPICKER).parents('mat-form-field')
                .should('have.class', 'mat-form-field-disabled');
            cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED).click();
            cy.get($dialogs.SNACKBAR).should('be.visible');
            cy.wait('@PUTchannel').then(xhr => {
                expect(xhr.request.body.settings).to.have.property('use_event_dates', true);
            })
        });
        it('edit all dates and times and save changes', () => {
            cy.get(elements.RELEASE_DATEPICKER).focus().click();
            cy.contains('mat-calendar td', '24')
                .click();
            cy.get(elements.RELEASE_TIMEPICKER).focus()
                .click().clear().type('15:30{esc}');

            cy.get(elements.SALE_START_DATEPICKER).focus().click();
            cy.contains('mat-calendar td', '26')
                .click();
            cy.get(elements.SALE_START_TIMEPICKER).focus()
                .dblclick().clear().type('15:30{esc}');

            cy.get(elements.SALE_END_DATEPICKER).focus().click();
            cy.contains('mat-calendar td', '26')
                .click();
            cy.get(elements.SALE_END_TIMEPICKER).focus()
                .dblclick().clear().type('15:30{esc}');

            cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED).click();
            cy.wait('@PUTchannel').then(xhr => {
                expect(xhr.request.body.settings.release).to.have.property('date', '2020-02-24T15:30:00+01:00');
                expect(xhr.request.body.settings.sale).to.have.property('start_date', '2020-02-26T15:30:00+01:00');
                expect(xhr.request.body.settings.sale).to.have.property('end_date', '2030-02-26T15:30:00+01:00');
            })
        });
        it('shows errors when setting a wrong release date for a channel', () => {
            cy.get(elements.RELEASE_DATEPICKER)
                .click();
            cy.contains('mat-calendar td', '26')
                .click();
            cy.get(elements.RELEASE_DATEPICKER)
                .parents('mat-form-field')
                .find('mat-error')
                .should('be.visible');
        });
        it('edit the sales groups of a channel from all capacity to a given sale group', () => {
            cy.get(elements.QUOTA_SELECT_INPUT)
                .click();
            cy.get(elements.QUOTA_SELECT)
                .should('have.class', 'mat-mdc-radio-checked');
            cy.contains(elements.QUOTA_OPTION, 'Grupo base')
                .then(checkbox => {
                    if (checkbox.attr('aria-selected') === 'true') {
                        cy.log('Checkbox already checked');
                    } else {
                        cy.get(checkbox)
                            .click()
                            .should('have.attr', 'aria-selected', 'true');
                        cy.get(elements.QUOTA_CHECKBOX_ERROR)
                            .should('not.exist');
                        cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED).click();
                    }
                });
            cy.wait('@PUTchannel').then(xhr => {
                expect(xhr.request.body.quotas).to.be.an('array').that.does.include(16276);
                expect(xhr.request.body).to.have.property('use_all_quotas', false);
            });
        });
        // # TODO: Scenario: Edit Times using the UI
    });
    context('using channelMonoPortal', () => {
        let channel;
        beforeEach(() => {
            cy.getFromFixture('channels', 'channelMonoPortal').then(
                (chan) => {
                    channel = chan;
                    cy.contains(elements.CHANNEL_LIST_SIDEBAR, channel.name).click();
                    cy.intercept('PUT', `**/channels/${channel.id}`, { status: 204, response: {} })
                        .as('PUTchannel');
                }
            );
        });
        it('edit the sales groups of a channel from a given sale group to all capacity', () => {
            cy.get(elements.QUOTA_SELECT)
                .should('have.class', 'mat-mdc-radio-checked');
            cy.get(elements.QUOTA_ALL)
                .should('not.have.class', 'mat-mdc-radio-checked');
            cy.get(elements.QUOTA_ALL_INPUT)
                .click();
            cy.get(elements.QUOTA_ALL)
                .should('have.class', 'mat-mdc-radio-checked');
            cy.get(elements.QUOTA_SELECT)
                .should('not.have.class', 'mat-mdc-radio-checked');
            cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED).click();
            cy.wait('@PUTchannel').then(xhr => {
                expect(xhr.request.body.quotas).to.be.empty;
                expect(xhr.request.body).to.have.property('use_all_quotas', true);
            });
        });
        it('a sales group is required for a channel', () => {
            cy.get(elements.QUOTA_CHECKBOX_FIRST_CHECKED)
                .click();
            cy.get(elements.QUOTA_CHECKBOX_ERROR)
                .should('be.visible');
        });
        it("doesn't shows the secondary market operative in an event without it", () => {
            cy.get(elements.SEC_MKT_TOGGLE)
                .should('not.exist');
        });
    });
});
