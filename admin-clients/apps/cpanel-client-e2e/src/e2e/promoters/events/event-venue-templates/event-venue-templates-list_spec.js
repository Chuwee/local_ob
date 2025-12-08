import elements from './event-venue-templates-components';

describe('Tests of Event/Venue-templates list', () => {
	context('using event venueTmplsEvent of ECV entity and user entityMgrECVCy', () => {
		let event;
		before(() => {
			cy.fixture('events').then((events) => { event = events.venueTmplsEvent });
		});
		beforeEach(() => {
			cy.login('entityMgrECVCy');
			cy.intercept('GET', '**/v1/venue-templates?*').as('GETvenueTmpls');
			cy.intercept('GET', '**/venue-templates/*/price-types').as('GETpriceTypes')
			cy.intercept('GET', '**/venue-templates/*').as('GETvenueTmpl');
			cy.visit(`/events/${event.id}/venue-templates`);
		});
		it('sees the venue template list with the first template expanded and clicks the second template', () => {
			cy.wait(['@GETvenueTmpls', '@GETpriceTypes'], { timeout: 20000 })
				.spread(
					(xhrVenueTmpls, xhrPriceType) => {
						cy.get(elements.VENUE_TPLS_LIST).should('have.class', 'mat-drawer-opened');
						const venueName = xhrVenueTmpls.response.body.data[0].name;
						// below there's an example to format a Number to number with european thousands (1.000)
						// const venueCapacity = new Intl.NumberFormat('de-DE').format(xhrVenueTmpls.response.body.data[0].capacity);
						const venueID = xhrVenueTmpls.response.body.data[0].id;
						const venuePriceZones = xhrPriceType.response.body.length;
						cy.contains(elements.VENUE_TPLS_LIST_ITEM, venueName)
							.should('have.class', 'mat-expanded')
							.within(() => {
								cy.get(elements.VENUE_TPLS_LIST_ITEM_ID)
									.should('have.text', venueID);
								cy.get(elements.VENUE_TPLS_LIST_ITEM_PZ)
									.should('have.text', venuePriceZones);
							})
						const secondVenue = xhrVenueTmpls.response.body.data[1];
						cy.intercept('GET', `**/venue-templates/${secondVenue.id}/price-types`).as('GETsecondVenuePriceTypes')
						cy.contains(elements.VENUE_TPLS_LIST_ITEM, secondVenue.name)
							.should('not.have.class', 'mat-expanded')
							.click()
							.should('have.class', 'mat-expanded')
							.within(() => {
								cy.get(elements.VENUE_TPLS_LIST_ITEM_ID)
									.should('have.text', secondVenue.id);
								cy.wait('@GETsecondVenuePriceTypes').then(xhr => {
									cy.get(elements.VENUE_TPLS_LIST_ITEM_PZ)
										.should('have.text', xhr.response.body.length);
								})
							})
					}
				)

		})
	})
})
