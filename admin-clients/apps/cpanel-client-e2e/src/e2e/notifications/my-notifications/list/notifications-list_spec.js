import elements from './notifications-list-components';
import $heading from '../../../shared/components/heading-components';

describe('Tests of Notifications/My Notifications list', () => {
	context('using an entity with notifications module enabled', () => {
		beforeEach(() => {
			cy.login('entityMgrECVCy');
			cy.intercept('GET', '**/v1/notifications?*').as('GETnotifications');
			cy.visit('/notifications');
		});
		it('applies status filter on the notifications list', () => {
			cy.intercept('GET', '**status=SENT').as('GETsentNotifications');
			cy.get(elements.FILTER_TOGGLE).click();
			cy.get(elements.FILTER_STATUS_INPUT).click();
			cy.getLiteral('NOTIFICATIONS.EMAIL_NOTIFICATION.STATUS_OPTS.SENT').then(literal => {
				cy.contains(elements.FILTER_STATUS_OPTION, literal).type('{esc}');
				cy.get(elements.FILTER_APPLY_BUTTON).click();
				cy.wait('@GETsentNotifications').then(xhr => {
					expect(xhr.request.url).to.contains('/notifications');
					expect(xhr.request.url).to.contains('limit=20');
					expect(xhr.request.url).to.contains('offset=0');
					expect(xhr.request.url).to.contains('aggs=true');
					expect(xhr.request.url).to.contains('sort=summary.sent_date%3Adesc');
					expect(xhr.request.url).to.contains('status=SENT');
				})
			})
		});
		it('searchs a notification by name and sorts list', () => {
			cy.intercept('GET', '**&q=**').as('GETnotificationsByName');
			cy.intercept('GET', '**sort=created_date**').as('GETnotificationsSorted');
			cy.get($heading.SEARCH_INPUT).type('evento ecv{enter}');
			cy.wait('@GETnotificationsByName').then(xhr => {
				expect(xhr.request.url).to.contains('q=evento%20ecv');
			})
			cy.get(elements.SORT_COL_CREATED_DATE).click();
			cy.wait('@GETnotificationsSorted').then(xhr => {
				expect(xhr.request.url).to.contains('sort=created_date%3Aasc');
			})
		});
		it('creates a new notification', () => {
			cy.intercept('POST', '**/notifications', { code: 'BD53TZR3563R6B' }).as('POSTnotification');
			cy.get(elements.NEW_NOTIFICATION_BTN).click();
			cy.get(elements.NEW_NOTIFICATION_NAME).type('Event changes');
			cy.get(elements.NEW_NOTIFICATION_CREATE).click();
			cy.wait('@POSTnotification').then(xhr => {
				expect(xhr.request.body).to.contains({ entity_id: 729, name: 'Event changes' });
			})
			cy.url().should('contain', 'notifications/BD53TZR3563R6B');
		});
	});
	context('using an entity with notifications module disabled', () => {
		it('is redirected to sales requests when a channel entity visits notifications url', () => {
			cy.login('entityMgrChannelCy');
			cy.visit('/notifications');
			cy.url().should('contain', '/sales-requests');
		});
	});
});
