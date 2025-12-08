import elements from './customer-transfer-components';
import $dialogs from '../../../../shared/components/dialogs-components';

describe('Transfer and Recovery session Tests', () => {
    let managerCustomer;
    before(() => {
        cy.fixture('viewers/customers').then((customers) => {
            managerCustomer = customers.managerECVCy;
        });
    });
    context('Using user entityMgrECVCy', () => {
        let ordersData, sessionsData;
        beforeEach(() => {
            cy.login('entityMgrECVCy');
            cy.intercept('GET', '**/orders/*/items/*').as('GETorderItems');
            cy.intercept('GET', '**/season-tickets/*/sessions**').as('GETstSessions');
            cy.intercept('POST', '**/orders/*/items/*/transfer', {}).as('POSTtransfer');
            cy.intercept('DELETE', '**/orders/*/items/*/transfer/*', { status: 204 }).as('DELtransfer');
            cy.visit(`/customers/${managerCustomer.id}/seat-management/transfer?entityId=${managerCustomer.entityId}`);
            cy.wait(['@GETorderItems', '@GETstSessions'], { timeout: 10000 }).spread((xhrOrders, xhrSessions) => {
                ordersData = xhrOrders.response.body;
                sessionsData = xhrSessions.response.body;
            });
        });
        it('recovers a transferred seat of a season-ticket', () => {
            const transferredItem = ordersData.transfer_data.sessions.filter(s => s.status == 'TRANSFERRED');
            const transferredSession = sessionsData.data.filter(s => s.session_id == transferredItem[0].session_id);

            cy.contains(elements.SESSION_NAME_CELL, transferredSession[0].session_name, { timeout: 10000 })
                .parent('mat-row').within(() => {
                    cy.getLiteral('CUSTOMER.TRANSFER.SEASON_TICKETS.SESSION_LIST.RECOVER_BUTTON').then(literal => {
                        cy.get(elements.RECOVER_SEAT_BUTTON)
                            .should('contain', literal)
                            .click();
                    })
                });
            cy.contains(elements.TRANSFERRED_INFO, transferredItem[0].data.email);
            cy.contains(elements.TRANSFERRED_INFO, transferredSession[0].session_name);
            cy.get($dialogs.DIALOG_BUTTON_CONFIRM).click();
            cy.wait('@DELtransfer').then(xhr => {
                expect(xhr.request.url).to.contain(transferredItem[0].session_id)
            })
        });
        it('transfers a seat of a season-ticket', () => {
            const inStItem = ordersData.transfer_data.sessions.filter(s => s.status == 'IN_SEASON');
            const inStSession = sessionsData.data.filter(s => s.session_id == inStItem[0].session_id);

            cy.contains(elements.SESSION_NAME_CELL, inStSession[0].session_name, { timeout: 10000 })
                .parent('mat-row').within(() => {
                    cy.getLiteral('CUSTOMER.TRANSFER.SEASON_TICKETS.SESSION_LIST.TRANSFER_BUTTON').then(literal => {
                        cy.get(elements.TRANSFER_SEAT_BUTTON)
                            .should('contain', literal)
                            .click();
                    })
                });
            cy.contains(elements.TRANSFERRED_INFO, inStSession[0].session_name);
            cy.get(elements.TRANSFER_NAME_INPUT).clear().type('Name');
            cy.get(elements.TRANSFER_SURNAME_INPUT).clear().type('Surname');
            cy.get(elements.RADIOBUTTON_INPUT).eq(0).click();
            cy.get(elements.TRANSFER_MAIL_INPUT).clear().type('qa@mail.com');
            cy.get($dialogs.DIALOG_BUTTON_CONFIRM).click();
            cy.wait('@POSTtransfer').then(xhr => {
                expect(xhr.request.body).to.have.property('session_id', inStItem[0].session_id);
                expect(xhr.request.body.transfer_data).to.contain({
                    email: 'qa@mail.com',
                    name: 'Name',
                    request_user_type: 'CPANEL',
                    send_type: 'EMAIL',
                    surname: 'Surname'
                });
            })
        });
        // TODO
        // Tranfer to download ticket
        // Resend email
        // Download ticket
    });
});
