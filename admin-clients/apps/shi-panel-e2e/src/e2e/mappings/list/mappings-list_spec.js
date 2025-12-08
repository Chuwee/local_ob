import elements from './mappings-list-components';
import * as utils from '../../shared/utils/utils';
import $dialogs from '../../shared/components/dialogs-components';
import $buttons from '../../shared/components/buttons-components';
import $filters from '../../shared/components/filters-components';

describe('Test mappings list', () => {
    const suppliers = {
        TEVO: 'TEVO',
        LOGITIX: 'LOGITIX'
    }
    context('Using shiUser user', () => {
        let mapping,
            supplier;
        before(() => {
            cy.getFromFixture('mappings/mappings', 'mapping').then(m => { mapping = m })
            cy.getFromFixture('mappings/mappings', 'supplier').then(s => { supplier = s })
        });
        beforeEach(() => {
            cy.login('shiUser')
            cy.intercept(/\/v1\/event-mappings\?(?!branch|sp)./).as('mappingsSearch')
            cy.visit('/mappings?noDate=true')
        })
        it('Get list sorted by date created', { tags: '@pgl' }, () => {
            cy.wait('@mappingsSearch', { timeout: 10000 }).then((xhr) => {
                expect(xhr.request.url).to.contain('sort=created%3Adesc')

                // Validate all items
                let mappings = xhr.response.body.data
                cy.get(elements.MAPPINGS_LIST_ROW)
                    .should('have.length', mappings.length)
                    .each(($el, index) => {
                        cy.wrap($el).find(elements.COLUMN_SHI_ID).contains(mappings[index].shi_id)
                    })
            })
        })
        it('Searches a random active mapping by supplier Id', () => {
            cy.wait('@mappingsSearch')
                .its('request.url')
                .should('contain', 'limit=20')
                .and('contain', 'aggs=true')
                .and('contain', 'offset=0')
                .and('contain', 'sort=created%3Adesc')
            cy.get($buttons.BUTTON_OPEN_FILTER).click()
            cy.get(elements.FILTER_STATUS).click()
            cy.contains($filters.FILTER_DROPDOWN_OPTION, 'Active').click()
            cy.get('body').click()
            cy.get(elements.OVERLAY).invoke('remove')
            cy.get($buttons.BUTTON_OK_FLAT).click({ force: true })
            cy.wait('@mappingsSearch').then((xhr) => {
                expect(xhr.request.url).to.contain('status=ACTIVE')

                let active_mappings = xhr.response.body.data

                let randomValue = utils.getRandomInt(active_mappings.length)
                let randomMappingId = active_mappings[randomValue].supplier_id
                cy.get($filters.SEARCH_INPUT).type(`${randomMappingId}{enter}`)
                cy.get(elements.MAPPINGS_LIST_ROW)
                    .find(elements.COLUMN_SUPPLIER_ID)
                    .should('have.length', 1)
                    .contains(randomMappingId)
            })
        })
        it('Applies Supplier filter in mappings list', () => {
            cy.wait('@mappingsSearch')
                .its('request.url')
                .should('contain', 'limit=20')
                .and('contain', 'aggs=true')
                .and('contain', 'offset=0')
                .and('contain', 'sort=created%3Adesc')
            cy.get($buttons.BUTTON_OPEN_FILTER).click()
            cy.get(elements.FILTER_SUPPLIER).click()
            cy.get($filters.FILTER_DROPDOWN_OPTION).should('have.length', 4)
            cy.contains($filters.FILTER_DROPDOWN_OPTION, 'Tevo').click()
            cy.get('body').click()
            cy.get(elements.FILTER_STATUS).click()
            cy.get($filters.FILTER_DROPDOWN_OPTION).should('have.length', 3)
            cy.contains($filters.FILTER_DROPDOWN_OPTION, 'Active').click()
            cy.get('body').click()
            cy.get($buttons.BUTTON_OK_FLAT).click({ force: true })
            cy.wait('@mappingsSearch').then((xhr) => {
                expect(xhr.request.url).to.contain('supplier=TEVO')

                let mappings = xhr.response.body.data
                cy.get(elements.MAPPINGS_LIST_ROW)
                    .should('have.length', mappings.length)
                    .each(($el, index) => {
                        cy.wrap($el).find(elements.COLUMN_SUPPLIER_ID).contains(mappings[index].supplier_id)
                        cy.wrap($el).find(elements.COLUMN_SUPPLIER)
                            .contains(suppliers[mappings[index].supplier])
                    })
                cy.get($filters.FILTER_LABELS).should('have.length', 2)
            })
        })
        it('Applies Status filter in mappings list', () => {
            cy.wait('@mappingsSearch')
                .its('request.url')
                .should('contain', 'limit=20')
                .and('contain', 'aggs=true')
                .and('contain', 'offset=0')
                .and('contain', 'sort=created%3Adesc')
            cy.get($buttons.BUTTON_OPEN_FILTER).click()
            cy.get(elements.FILTER_STATUS).click()
            cy.get($filters.FILTER_DROPDOWN_OPTION).should('have.length', 3)
            cy.contains($filters.FILTER_DROPDOWN_OPTION, 'Inactive').click()
            cy.get('body').click()
            cy.get($buttons.BUTTON_OK_FLAT).click({ force: true })
            cy.wait('@mappingsSearch').then((xhr) => {
                expect(xhr.request.url).to.contain('status=INACTIVE')

                let mappings = xhr.response.body.data
                cy.get(elements.MAPPINGS_LIST_ROW)
                    .should('have.length', mappings.length)
                    .each(($el, index) => {
                        cy.wrap($el).find(elements.COLUMN_SHI_ID).contains(mappings[index].shi_id)
                    })
                cy.get($filters.FILTER_LABELS).should('have.length', 1)
            })
        })
        it('Change the sorting in the mappings list', () => {
            cy.wait('@mappingsSearch')
                .its('request.url')
                .should('contain', 'limit=20')
                .and('contain', 'aggs=true')
                .and('contain', 'offset=0')
                .and('contain', 'sort=created%3Adesc')
            cy.get(elements.COLUMN_MAPPING_DATE).find($buttons.BUTTON_SORT_ARROW).click()
            cy.wait('@mappingsSearch').then((xhr) => {
                expect(xhr.request.url).to.contain('sort=created%3Aasc')

                let mappings = xhr.response.body.data
                cy.get(elements.MAPPINGS_LIST_ROW)
                    .should('have.length', mappings.length)
                    .each(($el, index) => {
                        cy.wrap($el).find(elements.COLUMN_SHI_ID).contains(mappings[index].shi_id)
                    })
            })
        })
        it('Export report from mappings list', () => {
            cy.wait('@mappingsSearch')
                .its('request.url')
                .should('contain', 'limit=20')
                .and('contain', 'aggs=true')
                .and('contain', 'offset=0')
                .and('contain', 'sort=created%3Adesc')
            cy.get($buttons.BUTTON_MORE_HORIZONTAL).click()
            cy.contains($buttons.BUTTON_MENU_ITEM, 'Export report').click()
            cy.get($buttons.BUTTON_OK_FLAT).click()
            cy.get($dialogs.SNACKBAR)
                .should('be.visible')
                .and('contain', 'export')
        })
        it('Check refresh button', () => {
            cy.wait('@mappingsSearch')
                .its('request.url')
                .should('contain', 'limit=20')
                .and('contain', 'aggs=true')
                .and('contain', 'offset=0')
                .and('contain', 'sort=created%3Adesc')
            cy.contains($buttons.BUTTON_ICON, 'autorenew').click()
            cy.wait('@mappingsSearch')
                .its('request.url')
                .should('contain', 'limit=20')
                .and('contain', 'aggs=true')
                .and('contain', 'offset=0')
                .and('contain', 'sort=created%3Adesc')
        })
        it('Change mapping status', () => {
            cy.intercept('PUT', '**/event-mappings/*', {}).as('mappingPUT');
            let frontTestMappingId = '1008888'
            cy.get($filters.SEARCH_INPUT).type(`${frontTestMappingId}{enter}`)
            cy.get(elements.MAPPINGS_LIST_ROW)
                .find(elements.COLUMN_SHI_ID)
                .should('have.length', 1)
                .contains(frontTestMappingId)
            cy.getLiteral('MAPPINGS.STATUS_OPTS.DISABLE').then(literal => {
                cy.get(elements.STATUS_SELECT, { timeout: 10000 }).first().click();
                cy.contains(elements.STATUS_SELECT_OPTION, literal).click();
                cy.get($dialogs.ALERT_DIALOG).should('be.visible');
                cy.get($dialogs.ACCEPT_WARNING_BTN).click();
                cy.wait('@mappingPUT').then(xhr => {
                    expect(xhr.request.body).to.eql({ status: 'DISABLE' })
                })
                cy.get($dialogs.SNACKBAR_SUCCESS).should('be.visible');
            })
        });
        it('Create new mapping', () => {
            cy.intercept('POST', '**/event-mappings', { code: mapping.code }).as('POSTmapping');
            cy.get(elements.NEW_MAPPING_BTN).click();
            cy.get(elements.NEW_MAPPING_SHI_ID).type(1001234);
            cy.get(elements.NEW_MAPPING_SUPPLIER).click();
            cy.contains($filters.DROPDOWN_OPTION, supplier.name, { matchCase: true }).click();
            cy.get(elements.NEW_MAPPING_SUPPLIER).should('have.text', supplier.name);
            cy.get(elements.NEW_MAPPING_SUPPLIER_ID).type(357892975);
            cy.get(elements.NEW_MAPPING_CREATE_BTN).click();
            cy.get($dialogs.SNACKBAR_SUCCESS).should('be.visible');
        })
        it('Delete created mapping', () => {
            cy.intercept('DELETE', '**/event-mappings/*', { statusCode: 204 }).as('DELETEmapping');
            cy.get($filters.SEARCH_INPUT).type(`${357892975}{enter}`)
            cy.get(elements.ACTION_DELETE_MAPPING_BUTTON, { timeout: 10000 }).first().click({ force: true });
            cy.get($dialogs.ALERT_BUTTON_CONFIRM).click();
            cy.wait(['@DELETEmapping', '@mappingsSearch']).spread(
                (del, mapping) => {
                    expect(del.request.url).to.contain(mapping.response.body.data[0].code)
                });
            cy.get($dialogs.SNACKBAR_SUCCESS).should('be.visible');
        });
    })
})
