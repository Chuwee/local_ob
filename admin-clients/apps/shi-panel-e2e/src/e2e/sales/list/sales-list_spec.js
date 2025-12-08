import elements from './sales-list-components';
import * as utils from '../../shared/utils/utils';
import $dialogs from '../../shared/components/dialogs-components';
import $buttons from '../../shared/components/buttons-components';
import $filters from '../../shared/components/filters-components';

describe('Tests of sales list', () => {
    const deliveryMethodSymbol = {
        ELECTRONIC: 'E',
        MOBILE_TRANSFER: 'T',
        MOBILE: 'M'
    };
    context('Using shiUser user', () => {
        beforeEach(() => {
            cy.login('shiUser')
            cy.intercept(/\/v1\/sales\?(?!branch|sp)./).as('salesSearch')
            cy.visit('/sales')
        });
        it('Get list sorted by created date', { tags: '@pgl' }, () => {
            cy.wait('@salesSearch', { timeout: 10000 }).then((xhr) => {
                expect(xhr.request.url).to.contain('sort=created%3Adesc')

                // Validate aggregated data
                let aggData = xhr.response.body.aggregated_data
                let totalSales = aggData.overall[0].value
                let fullfilled = aggData.type.find(x => x.agg_value == 'FULFILLED')
                let sold = aggData.type.find(x => x.agg_value == 'SOLD')
                let soldWithError = aggData.type.find(x => x.agg_value == 'SOLD_WITH_ERROR')
                let locale = 'en';

                cy.get(elements.AGG_TOTAL_SALES)
                    .should('contain', totalSales ? totalSales.toLocaleString(locale) : 0);
                cy.get(elements.AGG_TOTAL_FULFILLED)
                    .should('contain', fullfilled ? fullfilled.agg_metric[0].value.toLocaleString(locale) : 0);
                cy.get(elements.AGG_TOTAL_SOLD_WITH_ERROR)
                    .should('contain', soldWithError ? Math.floor(soldWithError.agg_metric[0].value / totalSales * 100) : 0)
                cy.get(elements.AGG_TOTAL_SOLD)
                    .should('contain', sold ? sold.agg_metric[0].value.toLocaleString(locale) : 0);

                // Validate all items
                let sales = xhr.response.body.data
                cy.get(elements.SALES_LIST_ROW)
                    .should('have.length', sales.length)
                    .each(($el, index) => {
                        cy.wrap($el).find(elements.COLUMN_SALE_ID).contains(sales[index].id)
                    })

            })
        });
        it('Searches a random sale by Id', () => {
            cy.wait('@salesSearch', { timeout: 10000 }).then((xhr) => {
                expect(xhr.request.url).to.contain('sort=created%3Adesc')
                let sales = xhr.response.body.data

                // Validate specific item
                let randomValue = utils.getRandomInt(sales.length)
                let randomSaleId = sales[randomValue].id
                cy.get($filters.SEARCH_INPUT).type(`${randomSaleId}{enter}`)
                cy.get(elements.SALES_LIST_ROW)
                    .find(elements.COLUMN_SALE_ID)
                    .should('have.length', 1)
                    .contains(randomSaleId)
            });
        });
        it('Applies Status filter in sales list', () => {
            cy.wait('@salesSearch', { timeout: 10000 })
            cy.get($buttons.BUTTON_OPEN_FILTER).click()
            cy.get(elements.FILTER_STATUS).click()
            cy.get($filters.FILTER_DROPDOWN_OPTION).should('have.length', 7)
            cy.contains($filters.FILTER_DROPDOWN_OPTION, 'Sold').click()
            cy.contains($filters.FILTER_DROPDOWN_OPTION, 'Fulfilled').click()
            cy.get('body').click()
            cy.get($buttons.BUTTON_OK).click({ force: true })
            cy.wait('@salesSearch').then((xhr) => {
                expect(xhr.request.url).to.contain('status=SOLD%2CFULFILLED')

                let sales = xhr.response.body.data
                cy.get(elements.SALES_LIST_ROW)
                    .should('have.length', sales.length)
                    .each(($el, index) => {
                        cy.wrap($el).find(elements.COLUMN_SALE_ID).contains(sales[index].id)
                    })
                cy.get($filters.FILTER_LABELS).should('have.length', 2)
            })
        })
        it('Change the sorting in the sales list', () => {
            cy.wait('@salesSearch', { timeout: 10000 })
            cy.get(elements.COLUMN_SALE_DATE).find($buttons.BUTTON_SORT_ARROW).click()
            cy.wait('@salesSearch').then((xhr) => {
                expect(xhr.request.url).to.contain('sort=created%3Aasc')

                let sales = xhr.response.body.data
                cy.get(elements.SALES_LIST_ROW)
                    .should('have.length', sales.length)
                    .each(($el, index) => {
                        cy.wrap($el).find(elements.COLUMN_SALE_ID).contains(sales[index].id)
                    })
            })
        })
        it('Export report from sales list', () => {
            cy.wait('@salesSearch', { timeout: 10000 })
            cy.contains($buttons.BUTTON_STROKED, 'file_download').click()
            cy.get($buttons.BUTTON_OK).click()
            cy.get($dialogs.SNACKBAR)
                .should('be.visible')
                .and('contain', 'export')
        })
        it('Check refresh button', () => {
            cy.wait('@salesSearch', { timeout: 10000 })
            cy.contains($buttons.BUTTON_ICON, 'autorenew').click()
            cy.wait('@salesSearch')
                .its('request.url')
                .should('contain', 'limit=20')
                .and('contain', 'aggs=true')
                .and('contain', 'offset=0')
                .and('contain', 'sort=created%3Adesc')
        })
        it('Tries to relaunch a received sale', () => {
            cy.wait('@salesSearch', { timeout: 10000 })
            cy.get($buttons.BUTTON_OPEN_FILTER).click()
            cy.get(elements.FILTER_STATUS).click()
            cy.get($filters.FILTER_DROPDOWN_OPTION).should('have.length', 7)
            cy.contains($filters.FILTER_DROPDOWN_OPTION, 'Received').click()
            cy.get('body').click()
            cy.get($buttons.BUTTON_OK).click({ force: true })
            cy.wait('@salesSearch').then((xhr) => {
                if (xhr.response.body.data.length) {
                    expect(xhr.request.url).to.contain('status=RECEIVED')
                    cy.get('body').click()
                    cy.get(elements.SALES_LIST_ROW, { timeout: 10000 }).first().click();
                    cy.get($buttons.BUTTON_OK).click({ force: true })
                    cy.get($dialogs.ALERT_DIALOG).should('be.visible');
                    cy.get($dialogs.ACCEPT_WARNING_BTN).click();
                    cy.get($dialogs.SNACKBAR_SUCCESS).should('be.visible');
                }
            })
        })
    })
})
