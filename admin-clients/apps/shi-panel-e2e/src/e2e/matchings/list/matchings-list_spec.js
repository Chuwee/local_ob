import elements from './matchings-list-components';
import $buttons from '../../shared/components/buttons-components';
import $filters from '../../shared/components/filters-components';
import $dialogs from '../../shared/components/dialogs-components';

describe('Test matchings list', () => {
    let replaceSpaces = (text => {
        return text.replace(/\s+/g, ' ');
    });
    context('Using shiUser user', () => {
        beforeEach(() => {
            cy.login('shiUser')
            cy.intercept(/\/v1\/suppliers\/LOGITIX\/event-matchings\?(?!branch|sp)./).as('matchingsSearch')
            cy.visit('/matchings')
        })
        xit('Get list sorted by status asc', { tags: '@pgl' }, () => {
            cy.wait('@matchingsSearch', { timeout: 10000 }).then((xhr) => {
                expect(xhr.request.url).to.contain('sort=status%3Aasc')

                // Validate all items
                let matchings = xhr.response.body.data
                cy.get(elements.MATCHINGS_LIST_ROW)
                    .should('have.length', matchings.length)
                    .each(($el, index) => {
                        cy.wrap($el).find(elements.COLUMN_EVENT_NAME).contains(replaceSpaces(matchings[index].supplier_event.name))
                    })
            })
        })
        xit('Apply Status filter in matchings list', () => {
            cy.wait('@matchingsSearch')
                .its('request.url')
                .should('contain', 'limit=20')
                .and('contain', 'aggs=true')
                .and('contain', 'offset=0')
                .and('contain', 'sort=status%3Aasc')
            cy.get($buttons.BUTTON_OPEN_FILTER).click()
            cy.get(elements.FILTER_SELECT).first().click()
            cy.get($filters.FILTER_DROPDOWN_OPTION).should('have.length', 4)
            cy.contains($filters.FILTER_DROPDOWN_OPTION, 'Candidate').click()
            cy.get('body').click()
            cy.get($buttons.BUTTON_OK_FLAT).click({ force: true })
            cy.wait('@matchingsSearch').then((xhr) => {
                expect(xhr.request.url).to.contain('status=CANDIDATE')

                let matchings = xhr.response.body.data
                cy.get(elements.MATCHINGS_LIST_ROW)
                    .should('have.length', matchings.length)
                    .each(($el, index) => {
                        cy.wrap($el).find(elements.COLUMN_EVENT_NAME).contains(replaceSpaces(matchings[index].supplier_event.name))
                    })
                cy.get($filters.FILTER_LABELS).should('have.length', 1)
            })
        })
        xit('Change order in matchings list', () => {
            cy.wait('@matchingsSearch', { timeout: 10000 })
            cy.get(elements.COLUMN_EVENT_NAME).find($buttons.BUTTON_SORT_ARROW).click()
            cy.wait('@matchingsSearch').then((xhr) => {
                expect(xhr.request.url).to.contain('sort=supplier_event_name%3Aasc')

                let matchings = xhr.response.body.data
                cy.get(elements.MATCHINGS_LIST_ROW)
                    .should('have.length', matchings.length)
                    .each(($el, index) => {
                        cy.wrap($el).find(elements.COLUMN_EVENT_NAME).contains(replaceSpaces(matchings[index].supplier_event.name))
                    })
            })
        })
        xit('Export report from matchings list', () => {
            cy.wait('@matchingsSearch')
                .its('request.url')
                .should('contain', 'limit=20')
                .and('contain', 'aggs=true')
                .and('contain', 'offset=0')
                .and('contain', 'sort=status%3Aasc')
            cy.get($buttons.BUTTON_MORE_HORIZONTAL).click()
            cy.contains($buttons.BUTTON_MENU_ITEM, 'Export report').click()
            cy.get($buttons.BUTTON_OK_FLAT).click()
            cy.get($dialogs.SNACKBAR)
                .should('be.visible')
                .and('contain', 'export')
        })
        xit('Check refresh button', () => {
            cy.wait('@matchingsSearch')
                .its('request.url')
                .should('contain', 'limit=20')
                .and('contain', 'aggs=true')
                .and('contain', 'offset=0')
                .and('contain', 'sort=status%3Aasc')
            cy.contains($buttons.BUTTON_ICON, 'autorenew').click()
            cy.wait('@matchingsSearch')
                .its('request.url')
                .should('contain', 'limit=20')
                .and('contain', 'aggs=true')
                .and('contain', 'offset=0')
                .and('contain', 'sort=status%3Aasc')
        })
    })
})
