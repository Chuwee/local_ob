// Importacion no debe ser requerido la expiracion y sí debe ser requerido el pin en VG code+pin
// Importacion no puede automatizarse porque cypress solo soporta input[type=file] o drag-and-drop de fichero
import elements from './voucher-codes-list-components';
// import $dialogs from "../../../../../shared/components/dialogs-components";


describe('Tests of Vouchers/Voucher-codes List', () => {
    context(
        'with user ecvMgrECVCy in ECVEntityGiftCard',
        () => {
            let voucherWithPin;
            let voucherNoPin;
            before(() => {
                cy.fixture('channels/vouchers').then((vouchers) => {
                    voucherWithPin = vouchers.ECVEntityManualCodePin;
                    voucherNoPin = vouchers.ECVEntityGiftCard;
                });
            });
            beforeEach(() => {
                cy.login('ecvMgrECVCy');
            });
            it('exports csv with manual codes with pin', () => {
                cy.visit(
                    `/vouchers/${voucherWithPin.voucherGroupId}/voucher-codes`
                );
                cy.intercept(
                    'POST',
                    `**/voucher-groups/${voucherWithPin.voucherGroupId}/vouchers/exports`,
                    { status: 202, response: { 'export_id': 'fb897bd2-170f-4648-a872-7f5da238c9b9' } }
                ).as('POSTexport');
                cy.get(elements.ACTIONS_BTN).click();
                cy.get(elements.EXPORT_CSV_BTN).click();
                cy.get(elements.EXPORT_FIELDS_ALL)
                    .should('have.class', 'mat-mdc-checkbox-checked');
                cy.get(elements.CONFIRM_EXPORT_BTN).click();
                cy.wait('@POSTexport').then(xhr => {
                    expect(xhr.request.body).to.eql(
                        {
                            'fields': [
                                { 'field': 'code', 'name': 'Código' },
                                { 'field': 'status', 'name': 'Estado' },
                                { 'field': 'email', 'name': 'Email' },
                                { 'field': 'balance', 'name': 'Saldo' },
                                { 'field': 'expiration', 'name': 'Caducidad' },
                                { 'field': 'usages.used', 'name': 'Usos' },
                                { 'field': 'usages.limit', 'name': 'Límite de usos' },
                                { 'field': 'pin', 'name': 'PIN' }
                            ],
                            'delivery': {
                                'type': 'EMAIL',
                                'properties': { 'address': 'qa.test.onebox+ecv.ecv.cy@gmail.com' }
                            },
                            'format': 'CSV'
                        }
                    )
                })
            });
            it('exports csv with manual codes with no pin', () => {
                cy.visit(
                    `/vouchers/${voucherNoPin.voucherGroupId}/voucher-codes`
                );
                cy.intercept(
                    'POST',
                    `**/voucher-groups/${voucherNoPin.voucherGroupId}/vouchers/exports`,
                    { status: 202, response: { 'export_id': 'fb897bd2-170f-4648-a872-7f5da238c9b9' } }
                ).as('new');
                cy.get(elements.ACTIONS_BTN).click();
                cy.get(elements.EXPORT_CSV_BTN).click();
                cy.get(elements.EXPORT_FIELDS_ALL)
                    .should('have.class', 'mat-mdc-checkbox-checked');
                cy.get(elements.CONFIRM_EXPORT_BTN).click();
                cy.wait('@new').then(xhr => {
                    expect(xhr.request.body).to.eql(
                        {
                            'fields': [
                                { 'field': 'code', 'name': 'Código' },
                                { 'field': 'status', 'name': 'Estado' },
                                { 'field': 'email', 'name': 'Email' },
                                { 'field': 'balance', 'name': 'Saldo' },
                                { 'field': 'expiration', 'name': 'Caducidad' },
                                { 'field': 'usages.used', 'name': 'Usos' },
                                { 'field': 'usages.limit', 'name': 'Límite de usos' }
                            ],
                            'delivery': {
                                'type': 'EMAIL',
                                'properties': { 'address': 'qa.test.onebox+ecv.ecv.cy@gmail.com' }
                            },
                            'format': 'CSV'
                        }
                    )
                })
            });
        }
    )
})
