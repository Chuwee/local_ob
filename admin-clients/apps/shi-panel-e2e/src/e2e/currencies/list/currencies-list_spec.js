import elements from './currencies-list-components';

describe('Test currencies list', () => {
    context('Using shiUser user', () => {
        beforeEach(() => {
            cy.login('shiUser')
            cy.intercept(/\/v1\/exchange-rates\?(?!branch|sp)./).as('currenciesSearch')
            cy.visit('/currencies')
        })
        it('Get list sorted by supplier', { tags: '@pgl' }, () => {
            cy.wait('@currenciesSearch', { timeout: 10000 }).then((xhr) => {
                expect(xhr.request.url).to.contain('sort=supplier%3Adesc')

                // Validate all items
                let currencies = xhr.response.body.data
                cy.get(elements.CURRENCIES_LIST_ROW)
                    .should('have.length', currencies.length)
                    .each(($el, index) => {
                        cy.wrap($el).find(elements.COLUMN_EXCHANGE_RATE).contains(currencies[index].rate)
                    })
            })
        })
    })
})
