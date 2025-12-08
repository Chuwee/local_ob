import elements from './entity-principal-info-components';


describe('Tests of Organizations/Entities/Entity/Principal Info', () => {
    context('with user operAdminCy and entity CyECV', () => {
        let entity;
        before(() => {
            cy.fixture('organizations/entities').then((entities) => {
                entity = entities.CyECV;
            });
        });
        beforeEach(() => {
            cy.login('operAdminCy');
            cy.visit(`/entities/${entity.id}/general-data/principal-info`);
        });
        it('sees all languages in entity details', () => {
            cy.intercept('GET', '**/languages**').as('GETlanguages')
            cy.wait('@GETlanguages', { timeout: 10000 }).then(xhr => {
                expect(xhr.request.url).not.to.contain('platform_language')
                cy.get(elements.LANGUAGE_CHECKBOX).should('have.length', xhr.response.body.length);
            });
        });
        it('short name does not show in non operator entities', () => {
            cy.get(elements.LANGUAGE_CHECKBOX, { timeout: 10000 });
            cy.get(elements.SHORT_NAME).should('not.exist');
        });
        it('sees all sections in entity details', () => {
            cy.get(elements.LANGUAGE_CHECKBOX, { timeout: 10000 });
            cy.get(elements.SHORT_NAME).should('not.exist');
        });
    });
    xcontext('with user adminEntityAdminCy and entityAdminCy ', () => {
        let entity;
        before(() => {
            cy.fixture('organizations/entities').then((entities) => {
                entity = entities.adminEntityAdminCy;
            });
        });
        beforeEach(() => {
            cy.login('entityAdminCy');
            cy.visit(`/entities/${entity.id}/general-data/principal-info`);
        });
        it('sees principal info and contact data in entity details', () => {
        });
    });
});
