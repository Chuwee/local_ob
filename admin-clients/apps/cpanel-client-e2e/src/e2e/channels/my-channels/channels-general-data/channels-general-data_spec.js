import elements from './channels-general-data_components';
import $bottombar from '../../../shared/components/bottombar-components';

// # Scenario: Edit details
// # Scenario: Multievent disabled and build not available for entity admin
// # TO DO: Scenario: Cancel button restores original state of inputs
// # TO DO: Scenario: When navigating to another tab unsaved changes are not saved

describe('Tests of Channels/Operative/General Data tab', () => {
	let channel;
	before(() => {
		cy.fixture('channels').then((channels) => {
			channel = channels.channelMonoPortal;
		});
	});
	context('using user channelMgrChannelCy', () => {
		beforeEach(() => {
			cy.login('channelMgrChannelCy');
			cy.intercept('PUT', `**/channels/${channel.id}`, {})
				.as('PUTchannel');
			cy.visit(`/channels/${channel.id}/general-data`);
		});
		it('edits general data of a channel', () => {
			cy.getLiteral('ENTITY.STATUS_OPTS.PENDING').then(literal => {
				cy.get(elements.NAME_INPUT).clear().type('Edited');
				cy.get(elements.STATUS_SELECT).click();
				cy.contains('mat-option', literal).click();
				cy.get(elements.BUILD_SELECT).should('not.exist');
				cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED).click();
				cy.wait('@PUTchannel').then(xhr => {
					const payload = xhr.request.body;
					expect(payload.name).to.equal('Edited');
					expect(payload.status).to.equal('PENDING');
				})
			})
		});
	});
	context('using user operAdminCy', () => {
		beforeEach(() => {
			cy.login('operAdminCy');
			cy.intercept('PUT', `**/channels/${channel.id}`, {})
				.as('PUTchannel');
			cy.visit(`/channels/${channel.id}/general-data`);
		});
		it('edits build and contact data of a channel', () => {
			// TODO: Edit contact data
			cy.get(elements.BUILD_SELECT).click();
			cy.contains('mat-option', 'mmc').click();
			cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED).click();
			cy.wait('@PUTchannel').then(xhr => {
				const payload = xhr.request.body;
				expect(payload.build).to.equal('MMC');
			})
		});
	});
});
