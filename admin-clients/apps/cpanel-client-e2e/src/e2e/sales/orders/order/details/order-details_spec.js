import elements from './order-details-components';
import $ordersList from '../../list/orders-list-components';

describe('Order Details Tests', () => {
    let channelPrtChannelMulti;
    let priceFormat = (number => {
        return number.toLocaleString('de-DE', { style: 'currency', currency: 'EUR', minimumFractionDigits: 2 });
    });
    let convertDateTimeFormat = (string => {
        let dateTime = new Date(string.substring(0, 19));
        let date = dateTime.toLocaleDateString('es-ES', { year: 'numeric', month: '2-digit', day: '2-digit' });
        let time = dateTime.toLocaleTimeString('es-ES', { hour: 'numeric', minute: '2-digit' });
        return `${date} - ${time}`;
    });
    let assertField = (field, container, value) => {
        cy.getLiteral(field).then(literal => {
            cy.contains(container, literal)
                .next()
                .should('contain', value)
        })
    };
    let getLocation = (allocation) => {
        let location = ''
        if (allocation.type == 'NUMBERED') {
            location = `${allocation.sector.name} ${allocation.row.name} ${allocation.seat.name}`
        } else if (allocation.event.type == 'ACTIVITY' || allocation.event.type == 'THEME_PARK') {
            location = `${allocation.price_type.name}`
        } else {
            location = `${allocation.sector.name} ${allocation.not_numbered_area.name}`
        }
        return `${location}`
    };
    before(() => {
        cy.fixture('channels').then((channels) => {
            channelPrtChannelMulti = channels['prtChannelMulti'];
        });
    });
    context('using user entityMgrQaa', () => {
        beforeEach(() => {
            cy.login('entityMgrQaa');
            cy.intercept(/\/orders\?(?!branch|sp)./).as('ordersSearch');
            cy.visit(`/transactions?orderType=PURCHASE&channel=${channelPrtChannelMulti.id}&noDate=true`);
        });
        it('See Ticket details related to Order', () => {
            cy.wait('@ordersSearch', { timeout: 10000 }).then(xhr => {
                const tixData = xhr.response.body.data[0];
                cy.intercept('GET', `**/orders/${tixData.code}`).as('getOrder');

                cy.get($ordersList.SEARCH_INPUT)
                    .type(`${tixData.code}{enter}`);
                cy.wait('@ordersSearch')
                    .its('request.url')
                    .should('include', `q=${tixData.code}`);

                cy.contains($ordersList.ORDERCODE_CELL, `${tixData.code}`)
                    .first()
                    .click();

                cy.wait('@getOrder').then(xhr => {
                    const order = xhr.response.body;
                    // Compra
                    cy.get(elements.HEADER_CONTAINER).should('contain', `Compra Nº ${order.code}`);

                    // Datos de la Transacción
                    assertField('FORMS.LABELS.TYPE', elements.ORDER_FIELD_DETAIL, order.type === 'PURCHASE' ? 'Compra' : '');
                    assertField('FORMS.LABELS.DATE', elements.ORDER_FIELD_DETAIL, convertDateTimeFormat(order.date));
                    assertField('FORMS.LABELS.USERNAME', elements.ORDER_FIELD_DETAIL, order.user.username);
                    assertField('FORMS.LABELS.TERMINAL', elements.ORDER_FIELD_DETAIL, order.terminal.name);
                    assertField('FORMS.LABELS.CHANNEL', elements.ORDER_FIELD_DETAIL, order.channel.name);
                    assertField('FORMS.LABELS.ENTITY', elements.ORDER_FIELD_DETAIL, order.channel.entity.name);
                    assertField('FORMS.LABELS.POINT_OF_SALE', elements.ORDER_FIELD_DETAIL, order.point_of_sale.name);

                    // Datos del cliente
                    cy.get('app-order-details-buyer-data').within(() => {
                        assertField('FORMS.LABELS.EMAIL', elements.ORDER_FIELD_DETAIL, order.buyer_data.email);
                        assertField('FORMS.LABELS.CLIENT_TYPE', elements.ORDER_FIELD_DETAIL, order.client_type === 'B2C' ? 'Particular' : 'Profesional');
                    })

                    // Desglose de Entradas
                    order.items.forEach(item => {
                        let hiddenBarcode = `****${item.ticket.barcode.code.substr(-5)} `;
                        cy.get(elements.ORDER_COLUMN_TICKET).should('contain', getLocation(item.ticket.allocation));
                        cy.get(elements.ORDER_COLUMN_BARCODE).should('contain', hiddenBarcode);
                        cy.get(elements.ORDER_COLUMN_RATE).should('contain', item.ticket.rate.name);
                        cy.get(elements.ORDER_COLUMN_PRICE).should('contain', priceFormat(item.price.final));
                    });

                    // Movimientos de Caja
                    order.payment_data.forEach(payment => {
                        cy.get(elements.PAYMENT_COLUMN_DATE).should('contain', convertDateTimeFormat(payment.date));
                        cy.get(elements.PAYMENT_COLUMN_PRICE).should('contain', priceFormat(payment.value));
                    });

                    // Importe
                    cy.getElByKey('ORDER.PRICE_DATA.BASE', elements.SIDEBAR_PRICE_ROW).should('contain', priceFormat(order.price.base));
                    cy.getElByKey('FORMS.LABELS.CHANNEL', elements.SIDEBAR_PRICE_ROW).should('contain', priceFormat(order.price.charges.channel));
                    cy.getElByKey('ORDER.PRICE_DATA.SURCHARGE.PROMOTER', elements.SIDEBAR_PRICE_ROW).should('contain', priceFormat(order.price.charges.promoter));
                    cy.getElByKey('FORMS.LABELS.TOTAL', elements.SIDEBAR_PRICE_ROW).should('contain', priceFormat(order.price.final));
                });
            });
        });
    });
});
