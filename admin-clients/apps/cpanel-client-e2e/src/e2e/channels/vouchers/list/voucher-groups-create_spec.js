import elements from './voucher-groups-list-components';


describe('Tests of Channels/new voucher-group creation', () => {
    context('using user operAdminCy', () => {
        beforeEach(() => {
            cy.login('operAdminCy');
            cy.visit('/vouchers');
        });
        it('creates a new manual voucher-group (stubbed)', () => {
            cy.intercept('POST', '**/voucher-groups', { 'id': 62 }).as('postVoucherGroup');
            cy.get(elements.NEW_VG_BUTTON).click();
            cy.get(elements.NEW_VG_ENTITY_SELECT).click();
            cy.get(elements.FILTER_INPUT_TYPE).type('Cypress ECV Entity{enter}');
            cy.get(elements.NEW_VG_NAME_INPUT).type('Test Name{enter}');
            cy.get(elements.NEW_VG_TYPE_INPUT).click();
            cy.get(elements.NEW_VG_OPTION).contains('Código Manual').click();
            cy.get(elements.NEW_VG_OPTION).should('not.be.visible');
            cy.get(elements.NEW_VG_VALIDATION_INPUT).click();
            cy.get(elements.NEW_VG_OPTION).contains('Código').click();
            cy.get(elements.NEW_VG_OPTION).should('not.be.visible');
            cy.get(elements.NEW_VG_CREATE_BUTTON).click();
            cy.wait('@postVoucherGroup').then(xhr => {
                expect(xhr.request.body).to.eql(
                    {
                        'currency_code': 'EUR',
                        'entity_id': 729,
                        'name': 'Test Name',
                        'type': 'MANUAL',
                        'validation_method': 'CODE',
                        'description': null
                    }
                );
            });
        });
    });
    context('using user entityMgrAvetQAA', () => {
        beforeEach(() => {
            cy.login('entityMgrAvetQAA');
            cy.visit('/vouchers');
        });
        it('creates a new external voucher-group (stubbed)', () => {
            cy.intercept('POST', '**/voucher-groups', { 'id': 3931 }).as('postVoucherGroup');
            cy.get(elements.NEW_VG_BUTTON).click();
            cy.get(elements.NEW_VG_NAME_INPUT).type('Test VG AVET{enter}');
            cy.get(elements.NEW_VG_TYPE_INPUT).click();
            cy.get(elements.NEW_VG_OPTION).contains('Extern').click();
            cy.get(elements.NEW_VG_OPTION).should('not.be.visible');
            cy.get(elements.NEW_VG_VALIDATION_INPUT)
                .should('contain', 'Socis AVET');
            cy.get(elements.NEW_VG_CREATE_BUTTON).click();
            cy.wait('@postVoucherGroup').then(xhr => {
                expect(xhr.request.body).to.eql(
                    {
                        'currency_code': 'EUR',
                        'entity_id': 594,
                        'name': 'Test VG AVET',
                        'type': 'EXTERNAL',
                        'validation_method': 'AVET_MEMBER_ID',
                        'description': null
                    }
                );
            });
        });
    });
})
