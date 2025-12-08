import elements from '../../list/tickets-list-components';
import $dialogs from '../../../../shared/components/dialogs-components';
import $topbar from '../../../../shared/components/topbar-components';

describe('Tests of Tickets Details', () => {
    context('using user operAdminQaa', () => {
        beforeEach(() => {
            cy.login('operAdminQaa');
            cy.intercept(/\/order-items\?(?!branch|sp)./).as('ticketsSearch');
            cy.visit('/tickets?state=PURCHASE&noDate=true');
        });
        it('downloads the ticket pdf and sees info message when pdf is not ready', () => {
            cy.intercept(/\/print\?merged=true/, {}).as('ticketsDownload');
            cy.wait('@ticketsSearch', { timeout: 10000 }).then(xhr => {
                const tixData = xhr.response.body.data[0];
                expect(tixData.ticket.barcode.code.length).to.be.at.least(14)
                const maskedBarcode = tixData.ticket.barcode.code.slice(-5).padStart(9, '*');
                cy.get(elements.SEARCH_INPUT)
                    .type(`${tixData.order.code}{enter}`);
                cy.wait('@ticketsSearch')
                    .its('request.url')
                    .should('include', `q=${tixData.order.code}`);
                cy.contains(elements.BARCODE_CELL, `${maskedBarcode}`)
                    .first()
                    .scrollIntoView()
                    .click();
                cy.get('.actions-bar button').eq(0).click();
                cy.wait('@ticketsDownload')
                    .its('request.url')
                    .should('contain', `orders/${tixData.order.code}/items/${tixData.id}/print?merged=true`);
            })
            cy.getLiteral('ACTIONS.SEE_TICKETS.KO.TITLE')
                .then(literal => {
                    cy.get($dialogs.INFO_DIALOG)
                        .should('contain', literal)
                })
        })
    });
    context('using user entityMgrECVCy', () => {
        beforeEach(() => {
            cy.login('entityMgrECVCy');
            cy.intercept(/\/order-items\?(?!branch|sp)./).as('ticketsSearch');
            // cy.visit(`/tickets?state=PURCHASE&noDate=true`);
        });
        it('access to a ticket detail directly and navigates to the tickets list', () => {
            cy.intercept('**/orders/4542ZYHZJE9W/items/127481379').as('getItem');
            cy.visit('/tickets/4542ZYHZJE9W-127481379');
            cy.wait('@getItem', { timeout: 10000 }).then(xhr => {
                const order = xhr.response.body;
                let assertField = (field, value) => {
                    cy.contains('.details-non-editable-field', field)
                        .should('contain', value)
                };
                assertField('Evento', order.ticket.allocation.event.name);
                assertField('Sesión', order.ticket.allocation.session.name);
                let hiddenBarcode = `****${order.ticket.barcode.code.substr(-5)} `;
                assertField('Código', hiddenBarcode);
                assertField('Tipo', order.ticket.rate.name);
                assertField('Tipo de localidad', order.ticket.allocation.type === 'NUMBERED' ? 'Numerada' : 'No numerada');
                assertField('Fila', order.ticket.allocation.row.name);
                assertField('Acceso', order.ticket.allocation.access.name);
                assertField('Sector', order.ticket.allocation.sector.name);
                assertField('Localidad', order.ticket.allocation.seat.name);
                assertField('Canal', order.channel.name);
                assertField('Entidad', order.channel.entity.name);
                assertField('Nombre', order.buyer_data.name);
                assertField('Apellidos', order.buyer_data.surname);
                assertField('Email', order.buyer_data.email);
            });
            cy.get($topbar.BACK_LINK).click();
            cy.url().should('contain', '/tickets?').and('contain', 'state=PURCHASE');
            cy.wait('@ticketsSearch')
                .its('request.url')
                .should('contain', 'state=PURCHASE')
                .and('contain', 'limit=20')
        })
    });
});
