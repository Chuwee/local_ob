import elements from './my-events-components';
import $dialogs from '../../../shared/components/dialogs-components';

describe('Tests of Event/My Events new event creation', () => {
    context('using user operAdminCy', () => {
        beforeEach(() => {
            cy.login('operAdminCy');
            cy.visit('/events');
        });
        it('creates a new (stubbed) event with operator user and gets redirected to it', () => {
            cy.intercept('POST', '**/events**', { 'id': 5304 }).as('postEvent');
            cy.get(elements.NEW_EVENT_BUTTON).click();
            cy.get(elements.NEW_EVENT_ENTITY_SELECT).click();
            cy.contains(elements.NEW_EVENT_SELECT_OPTION, 'Cypress ECV Entity').click();
            cy.get(elements.NEW_EVENT_PRODUCER_SELECT).click();
            cy.contains(elements.NEW_EVENT_SELECT_OPTION, 'Cypress ECV Entity').click();
            cy.get(elements.NEW_EVENT_NAME_INPUT).type('Test Name{enter}');
            cy.get(elements.NEW_EVENT_CATEGORY_SELECT).click()
            cy.get(elements.FILTER_INPUT_TYPE).type('{enter}');
            cy.get(elements.NEW_EVENT_CREATE_BUTTON).click();
            cy.wait('@postEvent').then(xhr => {
                expect(xhr.request.body).to.eql(
                    {
                        'name': 'Test Name',
                        'type': 'NORMAL',
                        'entity_id': 729,
                        'producer_id': 671,
                        'category_id': 15,
                        'currency_code': 'EUR'
                    }
                );
            });
            cy.url().should('include', '/events/5304/general-data/principal-info');
        });
        it('displays warnings in new event modal when required inputs are not valid', () => {
            cy.intercept('POST', '**/events**', { 'id': 5777 }).as('postEvent');
            cy.get(elements.NEW_EVENT_BUTTON).click();
            cy.get(elements.NEW_EVENT_CREATE_BUTTON).click();
            cy.get(elements.NEW_EVENT_ENTITY_SELECT)
                .should('have.class', 'ng-invalid');
            cy.get(elements.NEW_EVENT_NAME_INPUT)
                .should('have.class', 'ng-invalid');
            cy.get(elements.NEW_EVENT_CATEGORY_SELECT)
                .should('have.class', 'ng-invalid');

            cy.get(elements.NEW_EVENT_ENTITY_SELECT).click();
            cy.contains(elements.NEW_EVENT_SELECT_OPTION, 'Cypress ECV Entity').click();
            cy.get(elements.NEW_EVENT_ENTITY_SELECT)
                .should('not.have.class', 'ng-invalid');
            cy.get(elements.NEW_EVENT_PRODUCER_SELECT)
                .should('have.class', 'ng-invalid');
            cy.get(elements.NEW_EVENT_NAME_INPUT)
                .should('have.class', 'ng-invalid');
            cy.get(elements.NEW_EVENT_CATEGORY_SELECT)
                .should('have.class', 'ng-invalid');

            cy.get(elements.NEW_EVENT_PRODUCER_SELECT).click();
            cy.contains(elements.NEW_EVENT_SELECT_OPTION, 'Cypress ECV Entity').click();
            cy.get(elements.NEW_EVENT_PRODUCER_SELECT)
                .should('not.have.class', 'ng-invalid');

            cy.get(elements.NEW_EVENT_NAME_INPUT)
                .type('Test Name{enter}')
                .should('not.have.class', 'ng-invalid');
            cy.get(elements.NEW_EVENT_CATEGORY_SELECT)
                .should('have.class', 'ng-invalid');

            cy.get(elements.NEW_EVENT_CATEGORY_SELECT).click();
            cy.get(elements.FILTER_INPUT_TYPE).click();
            cy.contains(elements.NEW_EVENT_SELECT_OPTION, 'Ballet')
                .click()
                .should('not.have.class', 'ng-invalid');
        });
    });
    context('using user eventMgrEventCy', () => {
        beforeEach(() => {
            cy.login('eventMgrEventCy');
            cy.visit('/events');
        });
        it('creates a new (stubbed) activity with event-manager user and gets redirected to it', () => {
            cy.intercept('POST', '**/events**', { 'id': 5777 }).as('postEvent');
            cy.get(elements.NEW_EVENT_BUTTON).click();
            cy.get(elements.NEW_EVENT_ENTITY_SELECT).should('not.exist');
            cy.get(elements.NEW_EVENT_PRODUCER_SELECT)
                .should('not.have.class', 'mat-select-disabled')
                .click();
            cy.contains(elements.NEW_EVENT_SELECT_OPTION, 'Cypress Event Entity')
                .click();
            cy.get(elements.NEW_EVENT_NAME_INPUT).type('Test Name{enter}');
            cy.get(elements.NEW_EVENT_CATEGORY_SELECT).click();
            cy.get(elements.FILTER_INPUT_TYPE).click();
            cy.contains(elements.NEW_EVENT_SELECT_OPTION, 'Ballet').click();
            cy.get(elements.NEW_EVENT_TYPE_SELECT).click();
            cy.contains(elements.NEW_EVENT_SELECT_OPTION, 'Actividad').click();
            cy.get(elements.NEW_EVENT_CREATE_BUTTON).click();
            cy.wait('@postEvent').then(xhr => {
                expect(xhr.request.body).to.eql(
                    {
                        'name': 'Test Name',
                        'type': 'ACTIVITY',
                        'entity_id': 730,
                        'producer_id': 672,
                        'category_id': 15,
                        'currency_code': 'EUR'
                    }
                );
            });
            cy.url().should('include', '/events/5777/general-data/principal-info');
        });
        it('deletes an event and sees a warning dialog and a snackbar message', () => {
            cy.intercept('DELETE', '**/events/*', {
                status: 204,
                body: {}
            }).as('DELevent');
            cy.intercept('**/mgmt-api/v1/events?**').as('GETevents');
            cy.wait('@GETevents').then(xhr => {
                const firstEvent = xhr.response.body.data[0];
                cy.get(elements.DELETE_EVENT)
                    .first().click({ force: true });
                cy.get($dialogs.ALERT_DIALOG)
                    .should('contain', firstEvent.name);
                cy.get($dialogs.ALERT_BUTTON_CONFIRM)
                    .click();
                cy.wait('@DELevent').then(xhr => {
                    expect(xhr.request.url).to.contain(firstEvent.id);
                })
                cy.get('.snackbar-content')
                    .should('be.visible')
                    .and('contain', firstEvent.name);
            })
        })
        it('sees an error when deleting an event with sales', () => {
            cy.intercept('DELETE', '**/events/**', (req) => {
                req.reply({
                    statusCode: 403,
                    body: {
                        'code': 'FORBIDDEN_EVENT_DELETE',
                        'message': 'Event cant be deleted when already have sales'
                    }
                })
            }).as('DELevent');
            cy.intercept('**/mgmt-api/v1/events?**').as('GETevents');
            cy.wait('@GETevents').then(xhr => {
                console.log(xhr)
                const firstEvent = xhr.response.body.data[0];
                cy.get(elements.DELETE_EVENT)
                    .first().click({ force: true });
                cy.get($dialogs.ALERT_DIALOG)
                    .should('contain', firstEvent.name);
                cy.get($dialogs.ALERT_BUTTON_CONFIRM)
                    .click();
                cy.wait('@DELevent').then(xhr => {
                    expect(xhr.request.url).to.contain(firstEvent.id);
                })
                cy.get($dialogs.WARN_DIALOG)
                    .should('be.visible');
            })
        })
    });
})
