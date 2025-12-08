import elements from './channels-operative_components';
import $bottombar from '../../../shared/components/bottombar-components';
import $dialogs from '../../../shared/components/dialogs-components';

describe('Tests of Channels/Operative/Surcharges tab', () => {
    let channel,
        channelWithManyRanges;
    before(() => {
        cy.fixture('channels').then((channels) => {
            channel = channels.channelMonoPortal;
            channelWithManyRanges = channels.channelMultiPortal;
        });
    });
    context('using user channelMgrChannelCy and a single range channel', () => {
        beforeEach(() => {
            cy.login('channelMgrChannelCy');
            cy.intercept('POST', `**/channels/${channel.id}/surcharges`, {})
                .as('POSTsurcharges');
            cy.visit(`/channels/${channel.id}/operative/surcharges`);
        });
        it('creates a new generic surcharges range', () => {
            cy.get(elements.RANGES.GENERIC_SUR)
                .find(elements.RANGES.NEW_RANGE_BUTTON)
                .click();
            cy.get(elements.RANGES.NEW_RANGE_FROM_INPUT)
                .type('9.90{enter}');
            cy.get(elements.RANGES.GENERIC_SUR)
                .find(elements.RANGES.RANGE_TITLE)
                .then($titles => {
                    expect($titles[0].textContent).to.contain('0 - 9,89');
                    expect($titles[1].textContent).to.contain('9,90');
                });
            cy.get(elements.RANGES.GENERIC_SUR)
                .find(elements.RANGES.TABLE_ROWS).eq(1).as('newRange')
                .find(elements.RANGES.PERCENTAGE_CELL)
                .type('10');
            cy.get('@newRange').find(elements.RANGES.MIN_CELL).type('0.3');
            cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED)
                .click();
            cy.wait('@POSTsurcharges').then(xhr => {
                const genSurcharges = xhr.request.body[0];
                cy.wrap(genSurcharges)
                    .its('ranges')
                    .should('deep.include', {
                        from: 9.9,
                        values: {
                            fixed: 0,
                            percentage: 10,
                            min: 0.3,
                            max: null
                        }
                    })
            })
        });
        it("can't create a new range with wrong or empty values", () => {
            cy.get(elements.RANGES.GENERIC_SUR)
                .find(elements.RANGES.NEW_RANGE_BUTTON)
                .click();
            cy.get(elements.RANGES.NEW_RANGE_FROM_INPUT)
                .type('9.90{enter}');
            cy.get(elements.RANGES.GENERIC_SUR)
                .find(elements.RANGES.TABLE_ROWS).eq(1).as('newRange')
                .find(elements.RANGES.PERCENTAGE_CELL)
                .children('mat-form-field')
                .should('have.class', 'mat-form-field-invalid');
            cy.get('@newRange').find(elements.RANGES.VALUE_CELLS)
                .each($cell => {
                    cy.wrap($cell).type('-1');
                });
            cy.get(elements.RANGES.GENERIC_SUR)
                .find(elements.RANGES.TABLE_ROWS).eq(1)
                .find('.editable-cell mat-form-field')
                .each($cell => {
                    cy.wrap($cell)
                        .should('have.class', 'mat-form-field-invalid');
                });
            cy.get('@newRange').find(elements.RANGES.MIN_CELL).type(1);
            cy.get('@newRange').find(elements.RANGES.MAX_CELL).type(1)
                .children('mat-form-field')
                .should('have.class', 'mat-form-field-invalid');
        });
    });
    context('using user channelMgrChannelCy and a channel with many ranges', () => {
        beforeEach(() => {
            cy.login('channelMgrChannelCy');
            cy.intercept('GET', `**/channels/${channelWithManyRanges.id}/surcharges`)
                .as('GETsurcharges');
            cy.intercept('POST', `**/channels/${channelWithManyRanges.id}/surcharges`, {})
                .as('POSTsurcharges');
            cy.intercept('PUT', `**/channels/${channelWithManyRanges.id}`, {})
                .as('PUTchannel');
            cy.visit(`/channels/${channelWithManyRanges.id}/operative/surcharges`);
        });
        it('deletes a promotions range and resizes the lower one', () => {
            cy.get(elements.RANGES.PROMOTIONS_SUR)
                .find(elements.RANGES.RANGE_TITLE)
                .then($titles => {
                    expect($titles[0].textContent).to.contain('0 - 4,99');
                    expect($titles[1].textContent).to.contain('5,00');
                    expect($titles[1].textContent).to.contain('9,99');
                });
            cy.get(elements.RANGES.PROMOTIONS_SUR)
                .find(elements.RANGES.TABLE_ROWS).eq(1)
                .find(elements.RANGES.ROW_DELETE)
                .click({ force: true });
            cy.get(elements.RANGES.RESIZE_LOWER).click();
            cy.get(elements.RANGES.PROMOTIONS_SUR)
                .find(elements.RANGES.RANGE_TITLE)
                .then($titles => {
                    expect($titles[0].textContent).to.contain('0 - 9,99');
                    expect($titles[1].textContent).to.contain('10,00');
                    expect($titles[1].textContent).to.contain('19,99');
                });
            cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED)
                .click();
            cy.wait('@POSTsurcharges').then(xhr => {
                const promSurcharges = xhr.request.body[1];
                cy.wrap(promSurcharges)
                    .its('ranges')
                    .should('deep.include', {
                        from: 10,
                        values: {
                            fixed: 0,
                            percentage: 10,
                            min: null,
                            max: null
                        }
                    })
            })
        });
        it('deletes the last promotions range and cancels deletion', () => {
            cy.get(elements.RANGES.PROMOTIONS_SUR)
                .find(elements.RANGES.RANGE_TITLE)
                .then($titles => {
                    expect($titles[3].textContent).to.contain('20,00');
                });
            cy.get(elements.RANGES.PROMOTIONS_SUR)
                .find(elements.RANGES.TABLE_ROWS).last()
                .find(elements.RANGES.ROW_DELETE)
                .click({ force: true });
            cy.get(elements.RANGES.CONFIRM_DELETE).click();
            cy.get($bottombar.BOTTOMBAR_CANCEL_CHANGES_BTN)
                .click();
            cy.get($dialogs.DIALOG_BUTTON_CONFIRM).click();
            cy.get(elements.RANGES.PROMOTIONS_SUR)
                .find(elements.RANGES.RANGE_TITLE)
                .should($titles => {
                    expect($titles[3].textContent).to.contain('20,00');
                });
        });
        it('enables surcharges calculation after channel promotions option and disables promotions ranges in the same update', () => {
            cy.get(elements.RANGES.PROMOTIONS_ENABLER).click();
            cy.get(elements.RANGES.SURCH_AFTER_PROMS).click();
            cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED)
                .click();
            cy.wait('@PUTchannel').then(xhr => {
                expect(xhr.request.body).to.deep.equal(
                    {
                        settings: {
                            surcharges: {
                                calculation: 'AFTER_CHANNEL_PROMOTIONS'
                            }
                        }
                    }
                )
            });
            cy.wait('@POSTsurcharges').then(xhr => {
                expect(xhr.request.body[1]).to.have.property('enabled_ranges', false)
            })
        });
        it("can't create a new range if it already exists", () => {
            cy.getLiteral('RANGES.RANGE_ALREADY_EXISTS').then(literal => {
                cy.get(elements.RANGES.PROMOTIONS_SUR)
                    .find(elements.RANGES.NEW_RANGE_BUTTON)
                    .click();
                cy.get(elements.RANGES.NEW_RANGE_FROM_INPUT)
                    .type('5{enter}');
                cy.get($dialogs.DIALOG_TEXT)
                    .should('have.text', literal)
            })
        })
        it("can't create a new range with wrong values", () => {
            function createRangeWith(num) {
                cy.get(elements.RANGES.NEW_RANGE_FROM_INPUT)
                    .clear({ force: true })
                    .type(num)
                    .blur()
                    .should('have.css', 'caret-color', 'rgb(255, 82, 9)');
                cy.get(elements.RANGES.NEW_RANGE_CONFIRM)
                    .should('be.disabled');
            }
            cy.get(elements.RANGES.PROMOTIONS_SUR)
                .find(elements.RANGES.NEW_RANGE_BUTTON)
                .click();
            createRangeWith(-9);
            createRangeWith(12.001);
            createRangeWith(1234567890123456000000);
        });
    })
});
