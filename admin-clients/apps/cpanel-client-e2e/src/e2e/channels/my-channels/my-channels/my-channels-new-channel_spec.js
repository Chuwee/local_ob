import elements from './my-channels-components';
import $dialogs from '../../../shared/components/dialogs-components';

describe('Tests of Channels/new channel creation', () => {
    context('using user operAdminCy', () => {
        beforeEach(() => {
            cy.login('operAdminCy');
            cy.visit('/channels');
        });
        it('creates a new portal channel (stubbed) with operator user and gets redirected to it', () => {
            cy.intercept('POST', '**/channels**', { 'id': 1862 }).as('postChannel');
            cy.get(elements.NEW_CHANNEL_BUTTON).click();
            cy.get(elements.NEW_CHANNEL_ENTITY_SELECT).click({ force: true });
            cy.get(elements.FILTER_INPUT_TYPE).click().type('Cypress ECV Entity{enter}');
            cy.get(elements.NEW_CHANNEL_NAME_INPUT).type('Test Name{enter}');
            cy.get(elements.NEW_CHANNEL_TYPE_INPUT).click();
            cy.get(elements.NEW_CHANNEL_TYPE_OPTION).contains('OB Portal').click();
            cy.get(elements.NEW_CHANNEL_URL_INPUT).type('TestUrl');
            cy.get(elements.NEW_CHANNEL_CREATE_BUTTON).click();
            cy.wait('@postChannel').then(xhr => {
                expect(xhr.request.body).to.eql(
                    {
                        'entity_id': 729,
                        'name': 'Test Name',
                        'type': 'WEB',
                        'url': 'TestUrl'
                    }
                );
            });
            cy.url().should('include', '/channels/1862/general-data');
        });
        // it("displays warnings in new event modal when required inputs are not valid", () => {
        // 	cy.intercept('POST','**/events**', {"id": 5777}).as('postEvent');
        // 	cy.get(elements.NEW_EVENT_BUTTON).click();
        // 	cy.get(elements.NEW_EVENT_CREATE_BUTTON).click();
        // 	cy.get(elements.NEW_EVENT_ENTITY_SELECT)
        // 		.should('have.class', 'ng-invalid');
        // 	cy.get(elements.NEW_EVENT_NAME_INPUT)
        // 		.should('have.class', 'ng-invalid');
        // 	cy.get(elements.NEW_EVENT_CATEGORY_SELECT)
        // 		.should('have.class', 'ng-invalid');

        // 	cy.get(elements.NEW_EVENT_ENTITY_SELECT).click();
        // 	cy.get(elements.FILTER_INPUT_TYPE).type("Cypress ECV Entity{enter}");
        // 	cy.get(elements.NEW_EVENT_ENTITY_SELECT)
        // 		.should('not.have.class', 'ng-invalid');
        // 	cy.get(elements.NEW_EVENT_PRODUCER_SELECT)
        // 		.should('not.have.class', 'ng-invalid');
        // 	cy.get(elements.NEW_EVENT_NAME_INPUT)
        // 		.should('have.class', 'ng-invalid');
        // 	cy.get(elements.NEW_EVENT_CATEGORY_SELECT)
        // 		.should('have.class', 'ng-invalid');

        // 	cy.get(elements.NEW_EVENT_NAME_INPUT)
        // 		.type("Test Name{enter}")
        // 		.should('not.have.class', 'ng-invalid');
        // 	cy.get(elements.NEW_EVENT_CATEGORY_SELECT)
        // 		.should('have.class', 'ng-invalid');

        // 	cy.get(elements.NEW_EVENT_CATEGORY_SELECT).click();
        // 	cy.get(elements.FILTER_INPUT_TYPE).click();
        // 	cy.contains(elements.NEW_EVENT_SELECT_OPTION, 'Deportes')
        // 		.click({force:true})
        // 		.should('not.have.class', 'ng-invalid');
        // });
    });
    context('using user channelMgrChannelCy', () => {
        beforeEach(() => {
            cy.login('channelMgrChannelCy');
            cy.visit('/channels');
        });
        // it("creates a new (stubbed) boxoffice channel with event-manager user and gets redirected to it", () => {

        // });
        it('deletes a channel and sees a warning dialog and a snackbar message', () => {
            cy.intercept('DELETE', '**/channels/*', {
                status: 204,
                body: {}
            }).as('DELchannel');
            cy.intercept('**/channels?**').as('GETchannels');
            cy.wait('@GETchannels').then(xhr => {
                const firstChannel = xhr.response.body.data[0];
                cy.get(elements.DELETE_CHANNEL)
                    .first().click({ force: true });
                cy.get($dialogs.ALERT_DIALOG)
                    .should('contain', firstChannel.name);
                cy.get($dialogs.ALERT_BUTTON_CONFIRM)
                    .click();
                cy.wait('@DELchannel').then(xhr => {
                    expect(xhr.request.url).to.contain(firstChannel.id);
                })
                cy.get('.snackbar-content')
                    .should('be.visible')
                    .and('contain', firstChannel.name);
            })
        })
        // it("sees an alert when deleting a channel with sales", () => {
        // cy.intercept('DELETE', '**/channels/**',{}).as('DELchannel');
        // cy.intercept('**/channels?**').as('GETchannels');
        // cy.wait('@GETchannels').then(xhr => {
        // 	const firstChannel = xhr.response.body.data[0];
        // 	cy.get(elements.DELETE_CHANNEL)
        // 		.first().click();
        // 	cy.get($dialogs.ALERT_DIALOG)
        // 		.should('contain', firstChannel.name);
        // 	cy.get($dialogs.ALERT_BUTTON_CONFIRM)
        // 		.click();
        // 	cy.wait('@DELchannel').then(xhr => {
        // 		expect(xhr.request.url).to.contain(firstChannel.id);
        // 	})
        // 	cy.get($dialogs.WARN_DIALOG)
        // 		.should('be.visible');
        // })
        // })
    });
})
