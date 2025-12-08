import elements from './notifications-settings-components';
import $bottombar from '../../shared/components/bottombar-components';
import $dialogs from '../../shared/components/dialogs-components';

describe('Tests of Notifications/Notifications settings', () => {
    context('using user operAdminCy and entity CyECV', () => {
        let entity;
        before(() => {
            cy.fixture('organizations/entities').then((entities) => {
                entity = entities.CyECV;
            });
        });
        beforeEach(() => {
            cy.login('operAdminCy');
            cy.intercept('GET', '**/v1/entities?*').as('GETentities');
            cy.visit('/notifications-settings');
        });
        it('sees and decrements de entity notification limits', () => {
            cy.intercept('GET', `**/v1/entities/${entity.id}`).as('GETentity');
            cy.intercept('PUT', `**/v1/entities/${entity.id}`, {}).as('PUTentity');
            cy.wait('@GETentities').then(xhr => {
                expect(xhr.request.url).to.contain('limit=999&offset=0&sort=name%3Aasc&fields=name');
                cy.get($dialogs.SELECT_ENTITY_INPUT).click();
                cy.contains(elements.DROPDOWN_OPTION, entity.name).click();
                cy.get(elements.SELECT_BTN).click();
                cy.wait('@GETentity').then(xhr => {
                    const sendLimit = xhr.response.body.settings.notifications.email.send_limit;
                    cy.get(elements.LIMIT_INPUT)
                        .should('have.value', sendLimit)
                        .clear()
                        .type(sendLimit - 10)
                        .should('have.value', sendLimit - 10);
                    cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED).click();
                    cy.wait('@PUTentity').then(xhrPut => {
                        expect(xhrPut.request.body).to.eql(
                            { settings: { notifications: { email: { send_limit: sendLimit - 10 } } } }
                        )
                    });
                    cy.get($dialogs.SNACKBAR_SUCCESS).should('be.visible');
                    cy.getLiteral('ENTITY.UPDATE_SUCCESS').then(literal => {
                        cy.get($dialogs.SNACKBAR_MSG).should('have.text', literal);
                    })
                })
            })
        })
    })
})
