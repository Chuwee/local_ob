import elements from './promotion-template-conditions-components';
import $bottombar from '../../../../shared/components/bottombar-components';
// import $dialogs from '../../../shared/components/dialogs-components';


describe('Tests of Promotion-templates / Conditions panel', () => {
	context('with user entityMgrECVCy in ECVBasicCollectiveTmpl', () => {
		let promoTmpl;
		before(() => {
			cy.fixture('events/promotion-templates').then(data => {
				promoTmpl = data.ECVBasicCollectiveTmpl;
			});
		});
		beforeEach(() => {
			cy.login('entityMgrECVCy');
			cy.intercept('PUT', '**/event-promotion-templates/*', {
				status: 204,
				response: {}
			}).as('PUTpromoTmpl');
			cy.intercept('GET', `**/event-promotion-templates/${promoTmpl.id}`).as('GETpromoTmpl');
			cy.visit(`/event-promotion-templates/${promoTmpl.id}/conditions`);
			cy.wait('@GETpromoTmpl', { requestTimeout: 10000 });
		});
		it('edits the conditions of a promotion-template', () => {
			cy.get(elements.EVENT_USER_MAX_LIMIT_CHECK).click();
			cy.get(elements.EVENT_USER_MAX_LIMIT_INPUT).type('2');
			cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED).click();
			cy.wait('@PUTpromoTmpl').then(xhr => {
				expect(xhr.request.body).to.nested.include(
					{ 'usage_limits.event_user_collective_max.enabled': true }
				);
				expect(xhr.request.body).to.nested.include(
					{ 'usage_limits.event_user_collective_max.limit': 2 }
				);
			})
		});
	});
})
