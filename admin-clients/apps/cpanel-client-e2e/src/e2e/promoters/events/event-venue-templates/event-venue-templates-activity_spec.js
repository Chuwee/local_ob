import elements from './event-venue-templates-components';

describe('Tests of Event/Venue-templates Activity', () => {
    context('using event activityEvent and user entityMgrEventCy', () => {
        let event;
        before(() => {
            cy.fixture('events').then((events) => { event = events.activityEvent });
        });
        beforeEach(() => {
            cy.login('entityMgrEventCy');
            cy.intercept('GET', '**/venue-templates/*/price-types').as('GETpriceTypes');
            cy.visit(`/events/${event.id}/venue-templates`);
        });
        it('adds a new price-type with and unexisting name', () => {
            cy.wait('@GETpriceTypes', { timeout: 15000 }).then((xhr) => {
                const priceType = xhr.response.body[0]
                cy.intercept('POST', '**/venue-templates/*/price-types', {
                    status: 201,
                    response: { id: priceType.id },
                }).as('POSTpriceTypes');
                cy.get(elements.ACT_PRICE_TYPE_LIST).first()
                    .should('have.value', priceType.name);
                cy.get(elements.ACT_PRICE_TYPE_ADD_NEW_BTN)
                    .should('be.disabled');
                cy.get(elements.ACT_PRICE_TYPE_ADD_NEW_INPUT)
                    .type(priceType.name);
                cy.get(elements.ACT_PRICE_TYPE_ADD_NEW_BTN)
                    .should('be.disabled');
                cy.get(elements.ACT_PRICE_TYPE_ADD_NEW_INPUT)
                    .clear()
                    .type('New Access');
                cy.get(elements.ACT_PRICE_TYPE_ADD_NEW_BTN)
                    .should('not.to.be.disabled')
                    .click();
                cy.wait('@POSTpriceTypes').then(post => {
                    expect(post.request.body).to.deep.equal(
                        {
                            name: 'New Access',
                            code: 'New Access'
                        }
                    )
                })
            })
        })
    })
})
