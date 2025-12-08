import elements from './channels-communication_components';
import $dialogs from '../../../shared/components/dialogs-components';

describe('Tests of Channels/Communication tab', () => {
	let portalChannel,
		boxofficeChannel;
	before(() => {
		cy.fixture('channels').then((channels) => {
			portalChannel = channels.channelMultiPortal;
			boxofficeChannel = channels.channelWithoutSalesReq;
		});
	});
	context('using user operAdminCy', () => {
		beforeEach(() => {
			cy.login('operAdminCy');
		});
		it('creates a new literal in a portal channel', () => {
			// it should redirect to /communication/literals
			cy.visit(`/channels/${portalChannel.id}/communication`);
			cy.intercept('POST', `**/channels/${portalChannel.id}/text-contents/languages/*`, {})
				.as('POSTtextContents');
			cy.get(elements.ADD_KEY_LITERAL_BTN)
				.click();
			cy.get(elements.NEW_LITERAL_KEY_INPUT)
				.type('NEW_KEY');
			cy.get(elements.NEW_LITERAL_VALUE_INPUT).first()
				.type('Nueva clave');
			cy.get($dialogs.DIALOG_BUTTON_CONFIRM)
				.click();
			cy.wait('@POSTtextContents').then(xhr => {
				expect(xhr.request.body).eql(
					[{ key: 'NEW_KEY', value: 'Nueva clave' }]
				);
			});
			cy.get($dialogs.SNACKBAR).should('be.visible');
		});
		it('sees not add new literal button in boxoffice channel', () => {
			// it should redirect to /communication/literals
			cy.visit(`/channels/${boxofficeChannel.id}/communication`);
			cy.get(elements.ADD_KEY_LITERAL_BTN).should('not.exist');
		});
	});
	context('using user entityMgrChannelCy', () => {
		beforeEach(() => {
			cy.login('entityMgrChannelCy');
		});
		it('sees not add new literal button with entity user', () => {
			// it should redirect to /communication/literals
			cy.visit(`/channels/${portalChannel.id}/communication`);
			cy.get(elements.ADD_KEY_LITERAL_BTN).should('not.exist');
		});
	});
})
