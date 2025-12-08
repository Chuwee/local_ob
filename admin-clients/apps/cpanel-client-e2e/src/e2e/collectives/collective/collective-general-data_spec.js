import elements from './collective-general-data-components';
import $bottombar from '../../shared/components/bottombar-components';


describe('Tests of Collectives/Collective/General-data', () => {
	context('using user operAdminCy', () => {
		beforeEach(() => {
			cy.login('operAdminCy');
			cy.intercept('GET', '**/collectives/*/entities').as('GETcollectiveEntities');
			cy.intercept('PUT', '**/collectives/*/entities', { statusCode: 204 }).as('PUTcollectiveEntities');
			cy.visit('/collectives/8127/general-data');
		});
		it('asigns an entity to an operator collective', () => {
			cy.wait('@GETcollectiveEntities').then(xhr => {
				const firstEntity = xhr.response.body[0];
				cy.get(elements.ENTITY_LIST_ITEM, { timeout: 10000 })
					.first()
					.should('have.text', firstEntity.name)
					.click()
                    .should('have.attr', 'aria-selected', 'true')
				cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED)
					.click();
				cy.wait('@PUTcollectiveEntities')
					.then(xhr => {
						expect(xhr.request.body.entities).to.include(firstEntity.id);
						expect(xhr.request.body.entities).to.not.include(728)//Cypress operator id must not be present
					})
			})
		})
	})
})
