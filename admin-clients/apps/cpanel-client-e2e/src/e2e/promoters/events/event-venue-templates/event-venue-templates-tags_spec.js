import elements from './event-venue-templates-components';
import $tmplTags from '../shared-components/event-venue-template-tags-components';
import $bottombar from '../../../shared/components/bottombar-components';

describe('Tests of Event/Venue-templates tags', () => {
	context('using event venueTmplsEvent of ECV entity and user entityMgrECVCy', () => {
		let event;
		before(() => {
			cy.fixture('events').then((events) => { event = events.venueTmplsEvent });
		});
		beforeEach(() => {
			cy.login('entityMgrECVCy');
			cy.intercept('PUT', '**/venue-templates/*/seats', {
				status: 204,
				response: {},
			}).as('PUTvenueTmpl');
			cy.visit(`/events/${event.id}/venue-templates`);
		});
		it('applies a blocking-reason to a seat and asserts the request', () => {
			cy.intercept('GET', '**blocking-reasons').as('BlockingReasons');
			cy.intercept('GET', '**/venue-templates/*/views/root').as('GETviews');
			cy.wait('@GETviews', { timeout: 15000 }).then((xhrViews) => {
				cy.get(`#${xhrViews.response.body.links[1].ref_id}`).click('bottom');
				cy.get(elements.VENUE_MAP_SEAT).first().as('seatSelected').click();
				cy.get($tmplTags.VENUE_TPLS_BLOCK_PANEL)
					.should('not.have.class', 'mat-expanded')
					.click()
					.should('have.class', 'mat-expanded')
					.find($tmplTags.VENUE_TPLS_TAG_OPTION)
					.first()
					.click();
				cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED)
					.click();
				cy.wait('@PUTvenueTmpl').then(xhr => {
					cy.get('@BlockingReasons').then(xhrBlockReasons => {
						expect(xhr.request.body).to.have.lengthOf(1);
						expect(xhr.request.body[0].blocking_reason).to.equal(xhrBlockReasons.response.body[0].id);
						expect(xhr.request.body[0].id).to.equal(100770022);
						expect(xhr.request.body[0].status).to.equal('PROMOTOR_LOCKED');
						expect(xhr.request.body[0].accessibility).not.to.be.null;
						expect(xhr.request.body[0].gate).not.to.be.null;
						expect(xhr.request.body[0].price_type).not.to.be.null;
						expect(xhr.request.body[0].quota).not.to.be.null;
						expect(xhr.request.body[0].visibility).not.to.be.null;
					})
				})
			})
		});
		it('sees the different tags of a seat with the right colors', () => {
			cy.intercept('GET', '**/venue-templates/*/views/root').as('GETviews');
			cy.wait('@GETviews', { timeout: 15000 }).then((xhrViews) => {
				cy.get(`#${xhrViews.response.body.links[4].ref_id}`).click();
				cy.get('circle[id="100770862"]').invoke('css', 'fill').should('eql', 'rgb(139, 195, 74)');
				cy.get('circle[id="100770863"]').invoke('css', 'fill').should('eql', 'rgb(255, 255, 255)');
				cy.get('circle[id="100770864"]').invoke('css', 'fill').should('eql', 'rgb(0, 0, 0)');
				cy.get($tmplTags.VENUE_TPLS_VISIBILITY_PANEL).click();
				cy.get('circle[id="100770865"]').invoke('css', 'fill').should('eql', 'rgb(148, 65, 55)');
				cy.get($tmplTags.VENUE_TPLS_ACCESSIBILITY_PANEL).click();
				cy.get('circle[id="100770865"]').invoke('css', 'fill').should('eql', 'rgb(148, 65, 55)');
				cy.get($tmplTags.VENUE_TPLS_PRICE_PANEL).click();
				cy.get('circle[id="100770865"]').invoke('css', 'fill').should('eql', 'rgb(233, 30, 99)');
				cy.get($tmplTags.VENUE_TPLS_QUOTA_PANEL).click();
				cy.get('circle[id="100770865"]').invoke('css', 'fill').should('eql', 'rgb(233, 30, 99)');
			})
		});
		it('sees current view name in breadcrumbs', () => {
			cy.intercept('GET', '**/venue-templates/*/views/root').as('GETrootView');
			cy.intercept('GET', /venue-templates\/\d+\/views\/\d+/).as('GetView');
			cy.wait('@GETrootView', { timeout: 20000 }).then(rootView => {
				cy.get(`#${rootView.response.body.links[1].ref_id}`).click('bottom');
				cy.wait('@GetView', { timeout: 20000 }).then(view => {
					const viewName = view.response.body.name;
					cy.get(elements.VENUE_TPLS_BREADCRUMB_CURRENT_VIEW).should('contain.text', viewName);
				})
			})
		});
	});
});
