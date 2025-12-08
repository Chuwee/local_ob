import elements from './event-sessions-capacity-components';
import $tmplTags from '../shared-components/event-venue-template-tags-components';
import $bottombar from '../../../shared/components/bottombar-components';


describe('Tests of Event/Sessions/Capacity', () => {
    context('using user entityMgrECVCy and event ecvMainEvent', () => {
        let event;
        before(() => {
            cy.fixture('events').then(events => { event = events.ecvMainEvent });
        });
        beforeEach(() => {
            cy.login('entityMgrECVCy');
            cy.intercept('PUT', '**/capacity/**', { status: 204, response: {} }).as('PUTcapacity');
            cy.visit(`/events/${event.id}/sessions/${event.sessions.noGraphic.id}/capacity`);
        });
        it('edits price-zone of a non-graphic-zone', () => {
            cy.get(`${elements.NG_SECTOR}${event.sessions.noGraphic.sector1.id}`)
                .click();
            cy.get(`${elements.NG_NNZ}${event.sessions.noGraphic.sector1.nnz1} mat-checkbox`)
                .should('not.have.class', 'mat-mdc-checkbox-checked');
            cy.get(`${elements.NG_NNZ}${event.sessions.noGraphic.sector1.nnz1} mat-checkbox`)
                .click();
            cy.get(`${elements.NG_NNZ}${event.sessions.noGraphic.sector1.nnz1} mat-checkbox`)
                .should('have.class', 'mat-mdc-checkbox-checked');
            cy.get($tmplTags.VENUE_TPLS_PRICE_PANEL).click().within(() => {
                cy.get($tmplTags.VENUE_TPLS_TAG_OPTION).last().then(priceZone => {
                    cy.wrap(priceZone).invoke('attr', 'id').as('priceZoneId');
                    cy.wrap(priceZone).click();
                })
            })
            cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED).click();
            cy.wait('@PUTcapacity').then(xhr => {
                cy.get('@priceZoneId').then(priceZoneId => {
                    expect(xhr.request.body[0]).to.have.property('id', event.sessions.noGraphic.sector1.nnz1);
                    expect(xhr.request.body[0]).to.have.property('price_type', Number(priceZoneId.replace('label-', '')));
                })
            });
        })
    })
})
