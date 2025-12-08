import elements from './orders-list-components';
import $topbar from '../../../shared/components/topbar-components';
import $dialogs from '../../../shared/components/dialogs-components';
import $filters from '../../../shared/components/filter-components';


describe('Tests of Orders (aka Transactions) List', () => {
    context('using user operAdminQaa', () => {
        beforeEach(() => {
            cy.login('operAdminQaa');
            cy.intercept(/\/orders\?(?!branch|sp)./).as('ordersSearch');
            cy.visit('/transactions?noDate=true');
        });
        it('get list sorted by date and searchs an order by order code', { tags: '@pgl' }, () => {
            cy.wait('@ordersSearch', { timeout: 10000 }).then((xhr) => {
                expect(xhr.request.url).to.contain('sort=date%3Adesc');
                const orderCode = xhr.response.body.data[0].code;
                cy.get(elements.SEARCH_INPUT).type(`${orderCode}{enter}`);
                cy.wait('@ordersSearch')
                    .its('request.url')
                    .should('include', `q=${orderCode}`);
                cy.get(elements.ORDER_LIST_ROW)
                    .should('have.length', 1)
                    .first()
                    .click();
                cy.getLiteral('TITLES.ORDER').then((title) => {
                    title = title.replace('{{orderCode}}', orderCode);
                    cy.get('.heading-container h1').should('contain', title);
                });
            });
        });
        it("applies Type filter in transaction's list", () => {
            cy.wait('@ordersSearch')
                .its('request.url')
                .should('contain', 'limit=20')
                .and('contain', 'aggs=true')
                .and('contain', 'offset=0')
                .and('contain', 'sort=date%3Adesc');
            cy.get(elements.OPEN_FILTER_BUTTON).click();
            cy.get(elements.FILTER_TYPE).click();
            cy.get(elements.FILTER_DROPDOWN_OPTION).should('have.length', 6);
            cy.contains(elements.FILTER_DROPDOWN_OPTION, 'Reserva').click();
            cy.contains(elements.FILTER_DROPDOWN_OPTION, 'Emisión').click();
            cy.get($filters.FILTER_APPLY_BTN).click({ force: true });
            cy.wait('@ordersSearch')
                .its('request.url')
                .should('contain', 'type=BOOKING%2CISSUE');
        });
        it('applies filters by Type: alive purchases only', () => {
            cy.wait('@ordersSearch');
            cy.get(elements.OPEN_FILTER_BUTTON).click();
            cy.get(elements.FILTER_TYPE).click();
            cy.contains(elements.FILTER_DROPDOWN_OPTION, 'Compra').click();
            cy.get(elements.OVERLAY).invoke('remove');
            cy.get(elements.FILTER_ORDER_ALIVE).click();
            cy.get($filters.FILTER_APPLY_BTN).click();
            cy.wait('@ordersSearch')
                .its('request.url')
                .should('contain', 'type=PURCHASE')
                .and('contain', 'order_alive=true');
        });
        // next one is disabled in cpanel until the client tests it
        xit('makes a sessions bulk refund', () => {
            cy.intercept('POST', '**/orders/massive-refunds', {
                fixture: 'orders/massive-refunds-response.json',
            }).as('POSTmassive-refunds');
            cy.get(elements.OPEN_ACTIONS_MENU).click();
            cy.get(elements.ACTIONS_MENU).should('be.visible');
            cy.getElByKey('ACTIONS.EXPORT.BTN', elements.ACTIONS_MENU).click();
            cy.get(elements.REFUNDS_MODAL).should('be.visible');
            cy.get(elements.REFUNDS_MODAL_STEP).should('have.text', '1');
            cy.get(elements.REFUNDS_ENTITY).click();
            cy.contains(
                elements.REFUNDS_DROPDOWM_OPTION,
                'Automation Agencias 1'
            ).click();
            cy.get(elements.REFUNDS_EVENT).click();
            cy.get(elements.REFUNDS_SEARCH_INPUT).type('soapui evento 2');
            cy.get(elements.REFUNDS_DROPDOWM_OPTION)
                .should('have.length', 1)
                .click();
            cy.get(elements.REFUNDS_SESSION).click();
            cy.contains(elements.REFUNDS_DROPDOWM_OPTION, 'Agencias').click();
            cy.get(elements.REFUNDS_CHANNEL).click();
            cy.contains(elements.REFUNDS_DROPDOWM_OPTION, 'TaqAuto').click();
            cy.get(elements.REFUNDS_TYPE_RADIO_SESSION).click();
            cy.get(elements.REFUNDS_CONTINUE_BTN).click();
            cy.get(elements.REFUNDS_MODAL_STEP).should('have.text', '2');
            // choose automatic refund
            cy.get(elements.REFUNDS_METHOD_RADIOS).eq(1).click();
            cy.get(elements.REFUNDS_CONTINUE_BTN).click();
            cy.get($dialogs.ALERT_DIALOG).should('be.visible');
            cy.get($dialogs.ALERT_BUTTON_CONFIRM).click();
            cy.wait('@POSTmassive-refunds').then((xhr) => {
                expect(xhr.request.body).to.eql({
                    event_entity_id: '503',
                    event_id: '2865',
                    session_id: ['121580'],
                    channel_id: ['643'],
                    refund_type: 'AUTOMATIC',
                    include_surcharges: false,
                    include_delivery: false,
                    include_insurance: false,
                });
            });
            cy.get(elements.REFUNDS_SUMMARY_MODAL).should('be.visible');
            cy.contains(elements.REFUNDS_SUMMARY_ITEM, 'Entidad')
                .next('span')
                .should('have.text', 'Automation Agencias 1');
            cy.contains(elements.REFUNDS_SUMMARY_ITEM, 'Evento')
                .next('span')
                .should('have.text', 'SoapUI Evento 2 Agencias 1');
            cy.contains(elements.REFUNDS_SUMMARY_ITEM, 'Sesión')
                .next('span')
                .should('have.text', 'Evento 3 Sesion Agencias 1');
            cy.contains(elements.REFUNDS_SUMMARY_ITEM, 'Canal')
                .next('span')
                .should('have.text', 'TaqAuto');
        });
    });
    context('using user entityMgrECV', () => {
        beforeEach(() => {
            cy.login('entityMgrECVCy');
            cy.intercept(/\/orders\?(?!branch|sp)./).as('ordersSearch');
            cy.visit('/transactions?noDate=true');
        });
        it('sees entity name in topbar', () => {
            cy.wait('@myself').then((xhr) => {
                const entityName = xhr.response.body.entity.name;
                cy.get($topbar.TOPBAR_ENTITY).should('include.text', entityName);
            });
        });
        it('does not see event-entity filter', () => {
            cy.wait('@ordersSearch');
            cy.get(elements.OPEN_FILTER_BUTTON).click();
            cy.get(elements.FILTER_CHANNEL).should('be.visible');
            cy.get(elements.FILTER_EVENT_ENT).should('be.visible');
            cy.get(elements.FILTER_CHANNEL_ENT).should('not.exist');
        });
        it('does not see sessions bulk refunds button', () => {
            cy.wait('@ordersSearch');
            cy.get(elements.OPEN_ACTIONS_MENU).click();
            cy.get(elements.ACTIONS_MENU).should('be.visible');
            cy.getElByKey('ACTIONS.EXPORT.BTN', elements.ACTIONS_MENU).should('be.visible');
            cy.getElByKey('ORDERS.MASSIVE_REFUND.BTN', elements.ACTIONS_MENU).should('not.exist');
        });
    });
});
