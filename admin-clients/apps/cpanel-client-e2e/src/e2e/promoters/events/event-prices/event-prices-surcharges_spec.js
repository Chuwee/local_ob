import elements from './event-prices-components';
import $bottombar from '../../../shared/components/bottombar-components';

describe('Tests of Event/Prices Surcharges panel', () => {
	context('with user eventMgrEventCy in pricesEvent', () => {
		let event;
		beforeEach(() => {
			cy.login('eventMgrEventCy');
			cy.fixture('events').then((events) => {
				event = events.pricesEvent;
				cy.intercept('POST', `**/events/${event.id}/surcharges`, {
					status: 204,
					response: {},
				}).as('POSTsurcharges');
				cy.visit(`/events/${event.id}/prices/surcharges`);
			});
		});
		it('edit all the tickets surcharges and limits informed to sales channels of an event', () => {
			let values = [0, 5, 0.5, 3.99];
			for (let i = 0; i < values.length; i++) {
				cy.get(elements.TICKETS_SURCH_TABLE)
					.find(elements.ROW_FIELDS)
					.eq(i)
					.click({ force: true })
					.clear({ force: true })
					.type(values[i]);
			}
			cy.get(elements.TICKETS_INFORM_CH)
				.click({ force: true })
				.should('be.checked');
			cy.get(elements.TICKETS_INFORM_LIMITS)
				.find('input')
				.eq(0)
				.click({ force: true })
				.type('0.5');
			cy.get(elements.TICKETS_INFORM_LIMITS)
				.find('input')
				.eq(1)
				.click({ force: true })
				.type('3.9');
			cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED)
				.click();
			cy.wait('@POSTsurcharges').then(xhr => {
				expect(xhr.request.body[0].limit).to.eql({ enabled: true, min: 0.5, max: 3.9 })
				expect(xhr.request.body[0].ranges[0].values).to.eql({ fixed: 0, percentage: 5, min: 0.5, max: 3.99 })
			})
		})
	})
})
