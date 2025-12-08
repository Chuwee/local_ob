import elements from './listings-list-components';
import * as utils from '../../shared/utils/utils';
import $dialogs from '../../shared/components/dialogs-components';
import $buttons from '../../shared/components/buttons-components';
import $filters from '../../shared/components/filters-components';

describe('Test listings list', () => {
    const suppliers = {
        TEVO: 'TEVO',
        LOGITIX: 'LOGITIX'
    }
    context('Using shiUser user', () => {
        beforeEach(() => {
            cy.login('shiUser')
            cy.intercept(/\/v1\/listings\?(?!branch|sp)./).as('listingsSearch')
            cy.visit('/listings')
        })
        it('Get list sorted by created date', { tags: '@pgl' }, () => {
            cy.wait('@listingsSearch', { timeout: 10000 }).then((xhr) => {
                expect(xhr.request.url).to.contain('sort=created%3Adesc')

                // Validate aggregated data
                let aggData = xhr.response.body.aggregated_data
                let totalListings = aggData.overall[0].value
                let imported = aggData.type.find(x => x.agg_value == 'IMPORTED')
                let error = aggData.type.find(x => x.agg_value == 'IMPORTED_WITH_ERROR')
                let deleted = aggData.type.find(x => x.agg_value == 'DELETED')
                let locale = 'en';

                cy.get(elements.AGG_TOTAL_LISTINGS)
                    .should('contain', totalListings ? totalListings.toLocaleString(locale) : 0);
                cy.get(elements.AGG_TOTAL_IMPORTED)
                    .should('contain', imported ? imported.agg_metric[0].value.toLocaleString(locale) : 0);
                cy.get(elements.AGG_TOTAL_ERROR)
                    .should('contain', error ? Math.floor(error.agg_metric[0].value / totalListings * 100) : 0)
                cy.get(elements.AGG_TOTAL_DELETED)
                    .should('contain', deleted ? deleted.agg_metric[0].value.toLocaleString(locale) : 0);
            })
        })
        it('Searches a random listing by Id', () => {
            cy.wait('@listingsSearch', { timeout: 10000 })
                .its('request.url')
                .should('contain', 'limit=20')
                .and('contain', 'aggs=true')
                .and('contain', 'offset=0')
                .and('contain', 'sort=created%3Adesc')
            cy.get($buttons.BUTTON_OPEN_FILTER).click()
            cy.get(elements.FILTER_STATUS).click()
            cy.contains($filters.FILTER_DROPDOWN_OPTION, 'Imported').click()
            cy.get('body').click()
            cy.get(elements.OVERLAY).invoke('remove')
            cy.get($buttons.BUTTON_OK).click({ force: true })
            cy.wait('@listingsSearch').then((xhr) => {
                expect(xhr.request.url).to.contain('status=IMPORTED')

                let imported_listings = xhr.response.body.data

                //Check on imported cuz Listings with Status error sometimes
                //don't have any id
                let randomValue = utils.getRandomInt(imported_listings.length)
                let randomListingId = imported_listings[randomValue].id
                cy.get($filters.SEARCH_INPUT).type(`${randomListingId}{enter}`)
                cy.get(elements.LISTINGS_LIST_ROW)
                    .find(elements.COLUMN_LISTING_ID)
                    .should('have.length', 1)
                    .contains(randomListingId)
            })
        })
        it('Applies Supplier filter in listings list', () => {
            cy.wait('@listingsSearch', { timeout: 10000 })
            cy.get($buttons.BUTTON_OPEN_FILTER).click()
            cy.get(elements.FILTER_SUPPLIER).click()
            cy.get($filters.FILTER_DROPDOWN_OPTION).should('have.length', 4)
            cy.contains($filters.FILTER_DROPDOWN_OPTION, 'TEvo').click()
            cy.get(elements.LISTBOX).invoke('remove')
            cy.get(elements.FILTER_STATUS).click({ force: true })
            cy.contains($filters.FILTER_DROPDOWN_OPTION, 'Imported').click()
            cy.get(elements.LISTBOX).invoke('remove')
            cy.get($buttons.BUTTON_OK).click({ force: true })
            cy.wait('@listingsSearch').then((xhr) => {
                expect(xhr.request.url).to.contain('supplier=TEVO')

                let listings = xhr.response.body.data
                cy.get(elements.LISTINGS_LIST_ROW)
                    .should('have.length', listings.length)
                    .each(($el, index) => {
                        cy.wrap($el).find(elements.COLUMN_LISTING_ID).contains(listings[index].id)
                        cy.wrap($el).find(elements.COLUMN_SUPPLIER)
                            .contains(suppliers[listings[index].supplier])
                    })
                cy.get($filters.FILTER_LABELS).should('have.length', 2)
            })
        })
        it('Applies Status filter in listings list', () => {
            cy.wait('@listingsSearch', { timeout: 10000 })
            cy.get($buttons.BUTTON_OPEN_FILTER).click()
            cy.get(elements.FILTER_STATUS).click()
            cy.get($filters.FILTER_DROPDOWN_OPTION).should('have.length', 3)
            cy.contains($filters.FILTER_DROPDOWN_OPTION, 'Imported').click()
            cy.get('body').click()
            cy.get(elements.OVERLAY).invoke('remove')
            cy.get($buttons.BUTTON_OK).click({ force: true })
            cy.wait('@listingsSearch').then((xhr) => {
                expect(xhr.request.url).to.contain('status=IMPORTED')

                let listings = xhr.response.body.data
                cy.get(elements.LISTINGS_LIST_ROW)
                    .should('have.length', listings.length)
                    .each(($el, index) => {
                        cy.wrap($el).find(elements.COLUMN_LISTING_ID).contains(listings[index].id)
                    })
                cy.get($filters.FILTER_LABELS).should('have.length', 1)
            })
        })
        it('Change the sorting in the listings list', () => {
            cy.wait('@listingsSearch', { timeout: 10000 })
            cy.get(elements.COLUMN_LISTING_DATE).find($buttons.BUTTON_SORT_ARROW).click()
            cy.wait('@listingsSearch').then((xhr) => {
                expect(xhr.request.url).to.contain('sort=created%3Aasc')

                let listings = xhr.response.body.data
                cy.get(elements.LISTINGS_LIST_ROW)
                    .should('have.length', listings.length)
                    .each(($el, index) => {
                        cy.wrap($el).find(elements.COLUMN_EVENT_ID).contains(listings[index].event_id)
                    })
            })
        })
        it('Export report from listings list', () => {
            cy.wait('@listingsSearch', { timeout: 10000 });
            cy.get($buttons.BUTTON_MORE_HORIZONTAL).click()
            cy.contains($buttons.BUTTON_MENU_ITEM, 'Export report').click()
            cy.get($buttons.BUTTON_OK).click()
            cy.get($dialogs.SNACKBAR)
                .should('be.visible')
                .and('contain', 'export')
        })
        it('Check refresh button', () => {
            cy.wait('@listingsSearch', { timeout: 10000 });
            cy.contains($buttons.BUTTON_ICON, 'autorenew').click()
            cy.wait('@listingsSearch')
                .its('request.url')
                .should('contain', 'limit=20')
                .and('contain', 'aggs=true')
                .and('contain', 'offset=0')
                .and('contain', 'sort=created%3Adesc')
        })
    })
})
