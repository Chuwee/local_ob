import elements from './invoicing-generate-report-components';
import $bottombar from '../../../shared/components/bottombar-components';
import $dialogs from '../../../shared/components/dialogs-components';
import $calendar from '../../../shared/components/calendar-components';

describe('Tests of Operators/Invoicing/Generate report', () => {
	context('using user sysAdmin and entity FLC of España operator', () => {
		let entity;
		before(() => {
			cy.fixture('organizations/entities').then((entities) => {
				entity = entities.FLC;
			});
		});
		beforeEach(() => {
			cy.login('sysAdmin');
			cy.intercept('POST', '**/onebox-invoicing', {}).as('POSTinvoicing');
			cy.visit('/invoicing/generate-report');
		});
		it('requests invoice report generation', () => {
			cy.get(elements.OPERATOR_SELECT, { timeout: 15000 }).click();
			cy.contains(elements.DROPDOWN_OPTION, entity.operator.name).click({ force: true });
			cy.get(elements.OPERATOR_SELECT).should('have.text', entity.operator.name);
			cy.get(elements.ENTITY_SELECT).click();
			cy.contains(elements.DROPDOWN_OPTION, entity.name).click();
			cy.get(elements.ENTITY_SELECT).should('have.text', entity.name);
			cy.get(elements.EMAIL_INPUT).type('qa.test.onebox@gmail.com');
			cy.get(elements.DATE_FROM).click();
			cy.get($calendar.YEAR_MONTH_BTN).click();
			cy.contains($calendar.YEAR_CELL, '2022').click();
			cy.contains($calendar.MONTH_CELL, 'JUL.').click();
			cy.contains($calendar.DAY_CELL, '1').click();
			cy.get(elements.DATE_FROM).should('have.value', '01/07/2022');
			cy.get(elements.DATE_TO).click();
			cy.get($calendar.YEAR_MONTH_BTN).click();
			cy.contains($calendar.YEAR_CELL, '2022').click();
			cy.contains($calendar.MONTH_CELL, 'JUL.').click();
			cy.contains($calendar.DAY_CELL, '31').click();
			cy.get(elements.DATE_TO).should('have.value', '31/07/2022')
			cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED).click();
			cy.get($dialogs.INFO_DIALOG).should('be.visible');
			cy.getLiteral('INVOICING.GENERATE_REPORT.GENERATION_INFO_TITLE').then(literal => {
				cy.get($dialogs.DIALOG_TITLE_TEXT).should('have.text', literal);
			});
			cy.get($dialogs.INFO_BUTTON_CONFIRM).click();
			cy.wait('@POSTinvoicing').then(xhr => {
				expect(xhr.request.body).to.eql(
					{
						email: 'qa.test.onebox@gmail.com',
						entities_id: [entity.id],
						entity_code: 'LCX',
						from: '2022-06-30T22:00:00.000Z',
						operator_id: entity.operator.id,
						to: '2022-07-31T21:59:59.999Z',
						user_id: 1
					}
				)
			});
			cy.get($dialogs.SNACKBAR).should('be.visible');
			cy.getLiteral('INVOICING.GENERATE_REPORT.GENERATION_SUCCESS').then(literal => {
				cy.get($dialogs.SNACKBAR_MSG).should('have.text', literal);
			});
		})
	})
})
