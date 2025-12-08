import elements from './operators-list-components';
import $heading from '../../../shared/components/heading-components';


describe('Tests of Organizations/Operators list', () => {
	context('using user sysAdmin', () => {
		beforeEach(() => {
			cy.login('sysAdmin');
			cy.intercept('GET', /\/operators\?(?!branch|sp)./).as('GEToperators')
			cy.visit('/operators');
		});
		it('searchs an operator by name in operators list', () => {
			cy.wait('@GEToperators').then(xhr => {
				expect(xhr.request.url).to.contain('sort=name%3Aasc');
			});
			cy.get($heading.SEARCH_INPUT).type('qa{enter}');
			cy.wait('@GEToperators').then(xhr => {
				expect(xhr.request.url).to.contain('q=qa');
				xhr.response.body.data.forEach(operator => {
					expect(operator.name).to.match(/qa/i);
				})
			})
		});
		it('goes to the second page in operators list', () => {
			cy.wait('@GEToperators').then(xhr => {
				expect(xhr.request.url).to.contain('limit=20');
				expect(xhr.request.url).to.contain('offset=0');
			});
			cy.get($heading.NEXT_PAGE).click();
			cy.wait('@GEToperators').then(xhr => {
				expect(xhr.request.url).to.contain('offset=20');
				expect(xhr.response.body.metadata.offset).to.equal(20);
			})
		});
		it('goes to the details of an operator', () => {
			cy.intercept('GET', '**/operators/*').as('GEToperator');
			cy.contains(elements.SHORT_NAME_CELL, '_CY').click();
			cy.wait('@GEToperator').then(xhr => {
				expect(xhr.request.url).to.contain(728)
			})
		});
		it('creates an operator', () => {
			cy.intercept('POST', '**/operators', { 'id': 1000, 'password': 'superSecret' }).as('POSToperator');
			cy.get(elements.NEW_MODAL_OPEN).click();
			cy.get(elements.NEW_NAME_INPUT).type('New Name');
			cy.get(elements.NEW_SHORTNAME_INPUT).type('New_Shortname');
			cy.get(elements.NEW_CURRENCY_INPUT).click();
			cy.contains(elements.NEW_DROPDOWN_OPTION, 'Euro').click();
			cy.get(elements.NEW_TIMEZONE_INPUT).click();
			cy.contains(elements.NEW_DROPDOWN_OPTION, 'GMT -12:00').click();
			cy.get(elements.NEW_SHARD_INPUT).click();
			cy.contains(elements.NEW_DROPDOWN_OPTION, 'TICKETS_1').click();
			cy.get(elements.NEW_LANGUAGE_INPUT).click();
			cy.contains(elements.NEW_DROPDOWN_OPTION, 'Castellano').first().click();
			cy.get(elements.NEW_GATEWAYS_INPUT).click();
			cy.contains(elements.NEW_DROPDOWN_OPTION, 'ECI_CORP').type('{esc}');
			cy.get(elements.NEW_CREATE_BTN).click();
			cy.wait('@POSToperator').then(xhr => {
				expect(xhr.request.body).to.eql(
					{
						name: 'New Name',
						short_name: 'New_Shortname',
						currency_code: 'EUR',
						olson_id: 'Etc/GMT+12',
						shard: 'TICKETS_1',
						gateways: ['ECI_CORP'],
						language_code: 'es-ES'
					}
				)
			})
			cy.get(elements.PASSWORD_DIALOG_PASS).should('contain', 'superSecret');
		});
	});
});
