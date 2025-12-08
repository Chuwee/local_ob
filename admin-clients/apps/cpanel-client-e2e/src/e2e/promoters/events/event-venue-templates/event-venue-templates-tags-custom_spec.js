import $tmplTags from '../shared-components/event-venue-template-tags-components';

describe('Tests of Event/Venue-templates custom tags', () => {
    context('using event venueTmplsEvent of ECV entity and venue-template without custom tags', () => {
        let event;
        before(() => {
            cy.fixture('events').then((events) => { event = events.venueTmplsEvent });
        });
        beforeEach(() => {
            cy.login('entityMgrECVCy');
            cy.visit(`/events/${event.id}/venue-templates`);
        });
        it('creates a new custom tag group', () => {
            cy.get($tmplTags.ADD_CUSTOM_TAG_GROUP, { timeout: 15000 }).click();
            cy.get($tmplTags.NEW_CUSTOM_TAG_NAME).type('Custom');
            cy.get($tmplTags.NEW_CUSTOM_TAG_CODE).type('CUS');
            cy.intercept('POST', '**/dynamic-tag-groups', { 'id': 45957 }).as('POSTcustomTagGroup');
            cy.intercept('GET', '**/dynamic-tag-groups', [{ 'id': 45957, 'name': 'Custom', 'code': 'CUS' }]).as('GETcustomTagGroups');
            cy.intercept('GET', '**/dynamic-tag-groups/*/tags', [{ 'id': 51710, 'name': 'Custom Tag 1', 'code': 'CT1', 'color': '16750592' }])
                .as('GETcustomTags');
            cy.get($tmplTags.CREATE_CUSTOM_TAG_BTN).click();
            cy.wait('@POSTcustomTagGroup').then(xhr => {
                expect(xhr.request.body).to.contain({ 'name': 'Custom', 'code': 'CUS' });
            });
            cy.get($tmplTags.FIRST_CUSTOM_TAG_PANEL).should('contain.text', 'Custom');
        });
    });
    context('using event ecvMainEvent of ECV entity and venue-templates with 1 and 2 custom tag group', () => {
        let event;
        before(() => {
            cy.fixture('events').then((events) => { event = events.ecvMainEvent });
        });
        beforeEach(() => {
            cy.login('ecvMgrECVCy');
        });
        it('creates a tag in a custom tag group and checks request sent and then renders response correctly', () => {
            cy.visit(`/events/${event.id}/venue-templates/${event.venueTemplates.venueTemplateCustomTag.id}/standard`);
            cy.get($tmplTags.FIRST_CUSTOM_TAG_PANEL, { timeout: 15000 }).click().within(() => {
                cy.contains($tmplTags.ADD_CUSTOM_TAG, 'add').click();
            })
            cy.get($tmplTags.NEW_CUSTOM_TAG_NAME).type('Custom Tag 2');
            cy.get($tmplTags.NEW_CUSTOM_TAG_CODE).type('CT2');
            cy.intercept('POST', '**/dynamic-tag-groups/*/tags', { 'id': 51711 })
                .as('POSTcustomTag');
            cy.intercept('GET', '**/dynamic-tag-groups/*/tags', [
                { 'id': 51710, 'name': 'Custom Tag 1', 'code': 'CT1', 'color': '16750592' },
                { 'id': 51711, 'name': 'Custom Tag 2', 'code': 'CT2', 'color': 'ffc107' }
            ]).as('GETcustomTags');
            cy.get($tmplTags.CREATE_CUSTOM_TAG_BTN).click();
            cy.wait('@POSTcustomTag').then(xhr => {
                expect(xhr.request.body).to.contain({
                    code: 'CT2',
                    name: 'Custom Tag 2'
                });
            });
            cy.get('#label-51710').within(() => {
                cy.get($tmplTags.CUSTOM_TAG_NAME)
                    .should('contain.text', 'Custom Tag 1')
                    .and('have.length', 1);
                cy.get($tmplTags.CUSTOM_TAG_COLOR)
                    .should('have.css', 'background-color', 'rgba(22, 117, 5, 0.573)');
            })
            cy.get('#label-51711').within(() => {
                cy.get($tmplTags.CUSTOM_TAG_NAME)
                    .should('contain.text', 'Custom Tag 2')
                    .and('have.length', 1);
                cy.get($tmplTags.CUSTOM_TAG_COLOR)
                    .should('have.css', 'background-color', 'rgb(255, 193, 7)');
            })
        });
        it('cannot add a third custom group and edits the name of the second custom tag group', () => {
            cy.visit(`/events/${event.id}/venue-templates/${event.venueTemplates.venueTemplateWith2Groups.id}/standard`);
            cy.getLiteral('VENUE_TPLS.ADD_GROUP_BTN_DISABLED_TOOLTIP').then(literal => {
                cy.get($tmplTags.ADD_CUSTOM_TAG_GROUP, { timeout: 15000 })
                    .should('have.class', 'disabled')
                    .trigger('mouseenter');
                cy.get('.mat-mdc-tooltip div', { timeout: 10000 }).should('have.text', literal);
            });
            cy.get($tmplTags.SECOND_CUSTOM_TAG_PANEL).click().within(() => {
                cy.contains($tmplTags.ADD_CUSTOM_TAG, 'edit').click();
            });
            cy.get($tmplTags.NEW_CUSTOM_TAG_NAME).clear().type('Edited');
            cy.intercept('PUT', '**/dynamic-tag-groups/*', {}).as('PUTcustomTagGroup');
            cy.get($tmplTags.CREATE_CUSTOM_TAG_BTN).click();
            cy.wait('@PUTcustomTagGroup').then(xhr => {
                expect(xhr.request.body).to.contain({ 'name': 'Edited' });
            });
        });
    });
});
