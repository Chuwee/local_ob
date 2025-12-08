import elements from './operator-general-data-components';


describe('Tests of Operators/Operator/General Data', () => {
	context('with user sysAdmin and entity operatorCy', () => {
		beforeEach(() => {
			cy.login('sysAdmin');
			cy.intercept('GET', '**/languages**').as('GETlanguages')
			cy.visit('/operators/728/general-data');
		});
		it('sees platform languages in entity details', () => {
			cy.wait('@GETlanguages', { timeout: 10000 }).then(xhr => {
				expect(xhr.request.url).to.contain('platform_language')
				cy.get(elements.LANGUAGE_INPUT).click();
				cy.get(elements.DROPDOWN_OPTION).should('have.length', xhr.response.body.length);
			});
		});
	});
});
