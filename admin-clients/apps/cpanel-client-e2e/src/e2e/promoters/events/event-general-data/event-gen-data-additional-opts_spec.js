import elements from './event-gen-data-components';
import $bottombar from '../../../shared/components/bottombar-components';

describe('Tests of Event/General Data/Additional Options with user operAdminCy', () => {
    let mainEvent;
    before(() => {
        cy.fixture('events').then(events => {
            mainEvent = events.ecvMainEvent;
        });
    });
    context('Using ECV Main Event and user entityMgrECVCy', () => {
        beforeEach(() => {
            cy.login('entityMgrECVCy');
            cy.intercept('GET', `**/events/${mainEvent.id}`).as('GETevent');
        });
        it('shows event additional options', () => {
            cy.visit(`/events/${mainEvent.id}/general-data/additional-options`);
            cy.wait('@GETevent').then(xhr => {
                expect(xhr.response.body).to.have.property('id', mainEvent.id);
                const haveOrNotVenueReports = xhr.response.body.settings.allow_venue_reports == true ? 'have.class' : 'not.have.class';
                cy.get(elements.CONFIG_VENUE_REPORTS)
                    .should(haveOrNotVenueReports, 'mat-mdc-checkbox-checked')
                const haveOrNotFestival = xhr.response.body.settings.festival == true ? 'have.class' : 'not.have.class';
                cy.get(elements.CONFIG_FESTIVAL)
                    .should(haveOrNotFestival, 'mat-mdc-checkbox-checked')
                const haveOrNotSessionPacks = xhr.response.body.settings.session_pack == 'DISABLED' ? 'not.have.class' : 'have.class';
                cy.get(elements.SESSIONPACK_ENABLE_CHECKBOX)
                    .should(haveOrNotSessionPacks, 'mat-mdc-checkbox-checked')
                const haveOrNotBookings = xhr.response.body.settings.bookings.enable == true ? 'have.class' : 'not.have.class';
                cy.get(elements.BOOKINGS_ENABLE_CHECKBOX)
                    .should(haveOrNotBookings, 'mat-mdc-checkbox-checked')
            })
        });
    });
});
