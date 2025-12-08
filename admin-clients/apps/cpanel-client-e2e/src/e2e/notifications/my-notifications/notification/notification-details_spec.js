import elements from './notification-details-components';

describe('Tests of Notifications/Notification details', () => {
	context('using user entityMgrECVCy and notification draftECVNotification', () => {
		let notification;
		before(() => {
			cy.fixture('notifications/notifications').then((notifications) => {
				notification = notifications.draftECVNotification;
			});
		});
		beforeEach(() => {
			cy.login('entityMgrECVCy');
			cy.intercept('GET', '**/v1/notification-recipients?*').as('GETnotification-recipients');
			cy.visit(`/notifications/${notification.code}`);
		});
		it('applies status filter on the notifications list', () => {
			cy.intercept('GET', '**/v1/events?**').as('GETevents');
			cy.wait('@GETevents').then(xhr => {
				expect(xhr.request.url).to.contain('entity_id=729');
				const event = xhr.response.body.data[0];
				cy.get(elements.EVENT_SELECT).click();
				cy.contains(elements.DROPDOWN_OPTION, event.name).click();
				cy.wait('@GETnotification-recipients').then(recipientsXhr => {
					expect(recipientsXhr.request.url).to.contain('entity_id=729')
						.and.to.contain(`event_id=${event.id}`)
					const totalRecipients = recipientsXhr.response.body.metadata.total;
					cy.get(elements.TOTAL_RECIPIENTS).should('have.text', totalRecipients)
				})
			})
		});
	})
})
