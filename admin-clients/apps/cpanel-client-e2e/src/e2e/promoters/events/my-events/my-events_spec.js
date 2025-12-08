import elements from './my-events-components';
import $chips from '../../../shared/components/chips-components';
import $filters from '../../../shared/components/filter-components';


describe('Tests of Event/My Events', () => {
    const title = 'Mis eventos';
    context('using user operAdminCy', () => {
        beforeEach(() => {
            cy.login('operAdminCy');
            cy.visit('/events');
            cy.location().should((loc) => {
                expect(loc.protocol).to.eq('https:')
                expect(loc.pathname).to.eq('/events')
                expect(loc.search).to.include('status=PLANNED,READY,IN_PROGRAMMING')
            });
        });
        it('sees entity column with operator user', () => {
            cy.get(elements.HEADER_IN_ENTITY_COL).should('be.visible');
        });
        it("applies Entity/Producer/Country/City/Event Type filter in event's list", { tags: '@pgl' }, () => {
            cy.get('h1').contains(title);
            cy.get($chips.FILTER_LIST).should('have.length', 3);
            cy.get(elements.ROW_LIST).should('have.length.gte', 5);
            cy.intercept('GET', /\/events\?(?!branch|sp)./, { statusCode: 200, resourceType: 'xhr' }).as('eventsSearch');
            cy.get($chips.REMOVE_ALL_FILTERS).click();
            cy.get($chips.FILTER_LIST).should('have.length', 0);
            cy.wait('@eventsSearch', { timeout: 10000 })
                .its('request.url')
                .should('not.contain', 'status=');
            cy.get(elements.ROW_LIST).should('have.length.gte', 5);
            cy.fixture('events/filterData').then((filters) => {
                cy.wrap(filters).each((filter) => {
                    cy.get(elements.OPEN_FILTER_BUTTON).click();
                    cy.get(elements[filter.filterSelector]).click();
                    cy.contains(elements.FILTER_DROPDOWN_OPTION, filter.filterOption).click();
                    cy.get($filters.FILTER_APPLY_BTN).click({ force: true });
                    cy.wait('@eventsSearch', { timeout: 10000 })
                        .its('request.url')
                        .should(
                            'contain',
                            filter.filterParam
                        );
                });
            });
        });
        it("applies Date filter in event's list", () => {
            cy.intercept('**start_date**').as('dateSearch');
            cy.get(elements.OPEN_FILTER_BUTTON).click();
            cy.get(elements.FILTER_STARTDATE_SELECT)
                .type('20/01/2025', { force: true })
            cy.get(elements.FILTER_STARTDATE_SELECT)
                .invoke('val').should('equal', '20/01/2025');
            cy.get(elements.FILTER_STARTDATE_SELECT)
                .should('have.css', 'caret-color', 'rgb(59, 160, 168)');
            cy.get(elements.FILTER_ENDDATE_SELECT)
                .type('01/10/2030', { force: true });
            cy.get(elements.FILTER_ENDDATE_SELECT)
                .invoke('val').should('equal', '01/10/2030');
            cy.get(elements.FILTER_ENDDATE_SELECT)
                .should('have.css', 'caret-color', 'rgb(59, 160, 168)');
            cy.get($filters.FILTER_APPLY_BTN).click({ force: true });
            cy.wait('@dateSearch')
                .its('request.url')
                .should(
                    'contain',
                    'start_date=gte%3A2025-01-19T23%3A00%3A00Z%2Clte%3A2030-10-01T21%3A59%3A59Z'
                );
        });
        it('finds an archived event by name', () => {
            cy.intercept('**q=finalizado**').as('qSearch');
            cy.intercept('**include_archived=true**').as('archivedSearch');
            cy.get($chips.REMOVE_ALL_FILTERS).click();
            cy.get('app-search-input input').type('finalizado{enter}');
            cy.get(elements.CELL_IN_STATUS_COL).should('not.exist');
            cy.wait('@qSearch').its('request.url').should('include', 'q=finalizado');
            cy.get(elements.CONTEXT_MSG).should('exist').invoke('text').should('contain', 'Lista vacía');
            cy.get(elements.OPEN_FILTER_BUTTON).click();
            cy.get(elements.FILTER_ARCHIVED_CHECKBOX).click();
            cy.get($filters.FILTER_APPLY_BTN).click();
            cy.wait('@archivedSearch').its('request.url').should('include', 'include_archived=true');
            cy.get($chips.CHIP).contains('Mostrar eventos archivados').should('exist');
            cy.get(elements.CELL_IN_NAME_COL).each(cell => {
                cy.wrap(cell).invoke('text').should('match', /finalizado/i)
            });
        });
    });
    context('using user eventMgrEventCy', () => {
        beforeEach(() => {
            cy.login('eventMgrEventCy');
            cy.visit('/events');
        });
        it('sees not entity column with entity user', () => {
            cy.get(elements.HEADER_IN_ENTITY_COL).should('not.exist');
        });
        it('adds an option of the Status filter to the default filters applied in the event list', () => {
            cy.get('h1').contains(title);
            cy.get($chips.FILTER_LIST).should('have.length', 3);
            cy.get(elements.ROW_LIST).should('have.length.gte', 5);
            cy.contains($chips.CHIP, 'Planificado').should('be.visible');
            cy.contains($chips.CHIP, 'Preparado').should('be.visible');
            cy.contains($chips.CHIP, 'En programación').should('be.visible');
            cy.intercept('GET', /\/events\?(?!branch|sp)./, { statusCode: 200, resourceType: 'xhr' }).as('eventsSearch');
            cy.get(elements.OPEN_FILTER_BUTTON).click();
            cy.get(elements.FILTER_STATUS_SELECT).click();
            cy.contains(elements.FILTER_DROPDOWN_OPTION, 'Cancelado').click();
            cy.get($filters.FILTER_APPLY_BTN).click({ force: true });
            cy.wait('@eventsSearch')
                .its('request.url')
                .should(
                    'contain',
                    'status=PLANNED%2CIN_PROGRAMMING%2CREADY%2CCANCELLED'
                );
            cy.contains($chips.CHIP, 'Planificado').should('be.visible');
            cy.contains($chips.CHIP, 'Preparado').should('be.visible');
            cy.contains($chips.CHIP, 'En programación').should('be.visible');
            cy.contains($chips.CHIP, 'Cancelado').should('be.visible');
        });
        it('removes an option of the Status filter to the default filters applied in the event list', () => {
            cy.get('h1').contains(title);
            cy.get($chips.FILTER_LIST).should('have.length', 3);
            cy.get(elements.ROW_LIST).should('have.length.gte', 5);
            cy.intercept('GET', /\/events\?(?!branch|sp)./, { statusCode: 200, resourceType: 'xhr' }).as('eventsSearch');
            cy.get(elements.OPEN_FILTER_BUTTON).click();
            cy.get(elements.FILTER_STATUS_SELECT).click();
            cy.contains(elements.FILTER_DROPDOWN_OPTION, 'Preparado').click();
            cy.get($filters.FILTER_APPLY_BTN).click({ force: true });
            cy.wait('@eventsSearch', { timeout: 10000 })
                .its('request.url')
                .should('not.contain', 'READY');
            cy.contains($chips.CHIP, 'Preparado').should('not.exist');
            cy.contains($chips.CHIP, 'Planificado').should('be.visible');
            cy.contains($chips.CHIP, 'En programación').should('be.visible');
        });
        xit('removes a Chip from Status filters applied in the event list', () => {
            cy.get('h1').contains(title);
            cy.get($chips.FILTER_LIST).should('have.length', 3);
            cy.get(elements.ROW_LIST).should('have.length.gte', 5);
            cy.intercept(/\/events\?(?!branch|sp)./).as('eventsSearch');
            cy.contains($chips.CHIP, 'Preparado')
                .find($chips.CHIP_REMOVE_ICON)
                .click();
            cy.wait('@eventsSearch', { timeout: 10000 })
                .its('request.url')
                .should('not.contain', 'READY');
            cy.contains($chips.CHIP, 'Preparado').should('not.exist');
            cy.contains($chips.CHIP, 'Planificado')
                .find($chips.CHIP_REMOVE_ICON)
                .click();
            cy.wait('@eventsSearch', { timeout: 10000 })
                .its('request.url')
                .should('not.contain', 'PLANNED');
            cy.contains($chips.CHIP, 'Planificado').should('not.exist');
            cy.contains($chips.CHIP, 'En programación').should('be.visible');
            cy.get(elements.CELL_IN_STATUS_COL).each((cell) => {
                cy.wrap(cell)
                    .invoke('text')
                    .should('match', /En programación/);
            });
        });
        it('pagination buttons are disabled when list is less or equal than 20 results', { tags: '@pgl' }, () => {
            cy.intercept(/\/events\?(?!branch|sp)./).as('eventsSearch');
            cy.get('h1').contains(title);
            cy.get($chips.FILTER_LIST).should('have.length', 3);
            cy.wait('@eventsSearch', { timeout: 10000 }).then(eventSearch => {
                expect(eventSearch.response.body.metadata.total).to.be.lessThan(21);
                cy.get(elements.PREVIOUS_PAGE_BUTTON).should('be.disabled');
                cy.get(elements.NEXT_PAGE_BUTTON).should('be.disabled');
            })
            cy.get(elements.ROW_LIST).should('have.length.gte', 3);
        });
    });
    context('using user operAdminQaa', () => {
        beforeEach(() => {
            cy.login('operAdminQaa');
            cy.visit('/events');
        });
        xit('goes to the second and last page when list is greater than 20 results', () => {
            cy.intercept(/\/events\?(?!branch|sp)./).as('eventsSearch');
            cy.get('h1').contains(title);
            cy.get($chips.FILTER_LIST).should('have.length', 3);
            cy.wait('@eventsSearch', { timeout: 10000 }).then(eventSearch => {
                expect(eventSearch.response.body.metadata.total).to.be.greaterThan(20);
                const offset = Math.floor(eventSearch.response.body.metadata.total / 20) * 20;
                cy.get(elements.PREVIOUS_PAGE_BUTTON)
                    .should('be.disabled');
                cy.get(elements.NEXT_PAGE_BUTTON)
                    .should('be.enabled')
                    .click();
                cy.get(elements.PREVIOUS_PAGE_BUTTON).should('be.enabled');
                cy.wait('@eventsSearch', { timeout: 10000 }).then(eventSearch => {
                    expect(eventSearch.response.body.metadata.offset).to.equal(20);
                });
                cy.visit(`/events?offset=${offset}`);
                cy.get(elements.PREVIOUS_PAGE_BUTTON).should('be.enabled');
                cy.get(elements.NEXT_PAGE_BUTTON).should('be.disabled');
            });
            cy.get(elements.ROW_LIST).should('have.length.gte', 5);
        });
    });
});
