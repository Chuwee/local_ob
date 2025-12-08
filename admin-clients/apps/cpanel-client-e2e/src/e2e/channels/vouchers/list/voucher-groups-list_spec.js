import elements from './voucher-groups-list-components';
import $chips from '../../../shared/components/chips-components';
import $heading from '../../../shared/components/heading-components';
import $filters from '../../../shared/components/filter-components';

describe('Tests of Channels / Vouchers list', () => {
    context('using user operAdminCy', () => {
        beforeEach(() => {
            cy.login('operAdminCy');
            cy.intercept('**/voucher-groups?**').as('voucherGroupsSearch');
            cy.visit('/vouchers');
        });
        it('sees entity column with operator user', () => {
            cy.get(elements.HEADER_IN_ENTITY_COL).should('be.visible');
        });
        it('applies and removes entity, type and status filters separately', () => {
            cy.fixture('channels/voucherGroups-filterData').then((filters) => {
                cy.wrap(filters)
                    .each((filter) => {
                        cy.intercept(`**${filter.filterParam}**`).as('filterSearch');
                        cy.get(elements.OPEN_FILTER_BUTTON).click();
                        cy.get(elements[filter.filterSelector]).click();
                        cy.contains(elements.FILTER_DROPDOWN_OPTION, filter.filterOption).click();
                        cy.get($filters.FILTER_APPLY_BTN).focus().click();
                        cy.wait('@filterSearch')
                            .its('request.url')
                            .should('contain', filter.filterParam);
                        cy.contains($chips.CHIP, filter.filterOption).should('be.visible');
                        cy.get($chips.REMOVE_ALL_FILTERS).click();
                    });
            });
        });
        // disabled because visibility of hidden elements its not triggering in cypress
        xit('sees list ordered by name and each item in the list can be deleted', () => {
            cy.wait('@voucherGroupsSearch').its('request.url').should('include', 'sort=name%3Aasc');
            cy.get(elements.ROW_IN_VG_LIST).each((row) => {
                cy.wrap(row).find(elements.DELETE_VG).should('be.visible');
            })
        });
    });
    context('using channelMgrChannelCy user', () => {
        beforeEach(() => {
            cy.login('channelMgrChannelCy');
            cy.intercept('**/voucher-groups?**').as('voucherGroupsSearch');
            cy.visit('/vouchers');
        });
        it('sees not entity column and entity select in filter with entity user', () => {
            cy.get(elements.HEADER_IN_ENTITY_COL).should('not.exist');
            cy.get(elements.OPEN_FILTER_BUTTON).click();
            cy.get(elements.FILTER_ENTITY_SELECT).should('not.exist');
        });
        it('finds nothing and then finds a voucher-group by name', () => {
            cy.intercept('**voucher-groups?*q=xxx**').as('qVoidSearch');
            cy.intercept('**voucher-groups?*q=pin**').as('qSearch');
            cy.get($heading.SEARCH_INPUT).type('xxx{enter}');
            cy.url().should('include', 'q=xxx');
            cy.wait('@qVoidSearch').its('request.url').should('include', 'q=xxx');
            cy.get(elements.CONTEXT_MSG).should('exist').invoke('text').should('contain', 'Lista vacía');
            cy.get($heading.SEARCH_REMOVE).click();
            cy.get($heading.SEARCH_INPUT).type('pin{enter}');
            cy.url().should('include', 'q=pin');
            cy.wait('@qSearch').its('request.url').should('include', 'q=pin');
            cy.get(elements.CELL_IN_NAME_COL).each(cell => {
                cy.wrap(cell).invoke('text').should('match', /pin/i)
            });
        });
        // it("activates a voucher-group using the dropdown in status column", () => {
        // });
    });
});
