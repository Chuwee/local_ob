import elements from './my-channels-components';
import $chips from '../../../shared/components/chips-components';
import $heading from '../../../shared/components/heading-components';
import $filters from '../../../shared/components/filter-components';

describe('Tests of Channels/My channels list', () => {
    context('using user operAdminQaa', () => {
        beforeEach(() => {
            cy.login('operAdminQaa');
            cy.intercept(/\/channels\?(?!branch|sp)./).as('channelsSearch');
            cy.visit('/channels');
        });
        it('sees entity column with operator user', () => {
            cy.get(elements.HEADER_IN_ENTITY_COL).should('be.visible');
        });
        it('goes to the second and last page when list is greater than 20 results', () => {
            cy.wait('@channelsSearch').then(channelsSearch => {
                expect(channelsSearch.response.body.metadata.total).to.be.greaterThan(20);
                const offset = Math.floor(channelsSearch.response.body.metadata.total / 20) * 20;
                cy.get(elements.PREVIOUS_PAGE_BUTTON)
                    .should('be.disabled');
                cy.get(elements.NEXT_PAGE_BUTTON)
                    .should('be.enabled')
                    .click();
                cy.get(elements.PREVIOUS_PAGE_BUTTON).should('be.enabled');
                cy.wait('@channelsSearch').then(channelsSearch => {
                    expect(channelsSearch.response.body.metadata.offset).to.equal(20);
                });
                cy.visit(`/events?offset=${offset}`);
                cy.get(elements.PREVIOUS_PAGE_BUTTON).should('be.enabled');
                cy.get(elements.NEXT_PAGE_BUTTON).should('be.disabled');
            })
        });
        it('applies and removes entity and type filters in channels list', () => {
            cy.fixture('channels/myChannels-filterData').then((filters) => {
                cy.wrap(filters)
                    .each((filter) => {
                        cy.intercept(`**${filter.filterParam}**`).as('filterSearch');
                        cy.get(elements.OPEN_FILTER_BUTTON).click();
                        cy.get(elements[filter.filterSelector]).click();
                        cy.contains(elements.FILTER_DROPDOWN_OPTION, filter.filterOption).click({ force: true });
                        cy.get($filters.FILTER_APPLY_BTN).focus().click();
                        cy.wait('@filterSearch')
                            .its('request.url')
                            .should('contain', filter.filterParam);
                        cy.contains($chips.CHIP, filter.filterOption).should('be.visible');
                        cy.get($chips.REMOVE_ALL_FILTERS).click();
                    });
            });
        });
    });
    context('using channelMgrChannelCy user', () => {
        beforeEach(() => {
            cy.login('channelMgrChannelCy');
            cy.intercept(/\/channels\?(?!branch|sp)./).as('channelsSearch');
            cy.visit('/channels');
        });
        it('sees not entity column and entity select in filter with entity user', () => {
            cy.get(elements.HEADER_IN_ENTITY_COL).should('not.exist');
            cy.get(elements.OPEN_FILTER_BUTTON).click();
            cy.get(elements.FILTER_ENTITY_SELECT).should('not.exist');
        });
        it('pagination buttons are disabled when list is less or equal than 20 results', () => {
            cy.wait('@channelsSearch').then(channelsSearch => {
                expect(channelsSearch.response.body.metadata.total).to.be.lessThan(21);
                cy.get(elements.PREVIOUS_PAGE_BUTTON).should('be.disabled');
                cy.get(elements.NEXT_PAGE_BUTTON).should('be.disabled');
            })
        });
        it('finds nothing and then finds a channel by name', () => {
            cy.intercept('**channels?*name=xxxxx**').as('qVoidSearch');
            cy.intercept('**channels?*name=mono**').as('qSearch');
            cy.get($heading.SEARCH_INPUT).type('xxxxx{enter}');
            cy.url().should('include', 'q=xxxxx');
            cy.wait('@qVoidSearch').its('request.url').should('include', 'name=xxxxx');
            cy.get(elements.CONTEXT_MSG).should('exist').invoke('text').should('contain', 'Lista vacía');
            cy.get($heading.SEARCH_REMOVE).click();
            cy.get($heading.SEARCH_INPUT).type('mono portal{enter}');
            cy.url().should('include', 'q=mono%20portal');
            cy.wait('@qSearch').its('request.url').should('include', 'name=mono%20portal');
            cy.get(elements.CELL_IN_NAME_COL).each(cell => {
                cy.wrap(cell).invoke('text').should('match', /mono portal/i)
            });
        });
        it('applies status filter in channels list', () => {
            cy.intercept('**status=PENDING**').as('filterSearch');
            cy.get(elements.OPEN_FILTER_BUTTON).click();
            cy.get(elements.FILTER_STATUS_PENDING).click();
            cy.get($filters.FILTER_APPLY_BTN).focus().click();
            cy.wait('@filterSearch')
                .its('request.url')
                .should('contain', 'status=PENDING');
            cy.contains($chips.CHIP, 'Pendiente').should('be.visible');
        });
        // TODO it("adds options to the filter and remove some chip", () => {
    });
});
