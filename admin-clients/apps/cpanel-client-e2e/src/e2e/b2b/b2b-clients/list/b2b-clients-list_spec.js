import elements from './b2b-clients-list-components';
import $dialogs from '../../../shared/components/dialogs-components'


describe('Tests of B2B/Clients list', () => {
	beforeEach(() => {
		cy.intercept(/\/entities\?(?!branch|sp)./).as('entitiesSearch');
		cy.intercept(/\/clients\?(?!branch|sp)./).as('clientsSearch');
	});
	context('using user operAdminCy', () => {
		beforeEach(() => {
			cy.login('operAdminCy');
			cy.visit('/b2b-clients');
		});
		it('selects an entity and sees its b2b-clients', { tags: '@pgl' }, () => {
			cy.wait('@entitiesSearch').then(xhr => {
				const entity = xhr.response.body.data[0];
				cy.get($dialogs.SELECT_ENTITY_INPUT).click();
				cy.contains($dialogs.SELECT_OPTION, entity.name).click();
				cy.get($dialogs.SELECT_ENTITY_INPUT).should('have.text', entity.name);
				cy.get($dialogs.DIALOG_BUTTON_CONFIRM).click();
			})
			cy.wait('@clientsSearch').then(xhr => {
				const client = xhr.response.body.data[0];
				cy.get(elements.CLIENT_NAME_CELL).first().should('have.text', client.name);
			})
		});
	});
	context('using user entityMgrECVCy', () => {
		beforeEach(() => {
			cy.login('entityMgrECVCy');
			cy.visit('/b2b-clients');
		});
		it('sees own entity b2b-clients', () => {
			cy.wait('@clientsSearch').then(xhr => {
				expect(xhr.response.body.metadata.total).to.be.greaterThan(0);
				const client = xhr.response.body.data[0];
				cy.get(elements.CLIENT_NAME_CELL).first().should('have.text', client.name);
			})
		});
	});
});
