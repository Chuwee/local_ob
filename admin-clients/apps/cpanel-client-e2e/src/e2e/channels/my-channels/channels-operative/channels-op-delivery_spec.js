import elements from './channels-operative_components';
import $bottombar from '../../../shared/components/bottombar-components';

describe('Tests of Channels/Operative/Delivery methods tab', () => {
	let channel;
	before(() => {
		cy.fixture('channels').then((channels) => {
			channel = channels.channelMonoPortal;
		});
	});
	context('using user channelMgrChannelCy', () => {
		beforeEach(() => {
			cy.login('channelMgrChannelCy');
		});
		it('edits a delivery method', () => {
			cy.intercept('PUT', `**/channels/${channel.id}/delivery`, {})
				.as('PUTdelivery');
			cy.visit(`/channels/${channel.id}/operative/delivery-methods`);
			cy.contains(elements.DELIVERY_METHODS.DELIVERY_METHOD_ROW, 'Recogida en recinto')
				.find(elements.DELIVERY_METHODS.CHECKBOX)
				.click();
			cy.contains(elements.DELIVERY_METHODS.DELIVERY_METHOD_ROW, 'Recogida en recinto')
				.find(elements.DELIVERY_METHODS.PRICE)
				.type('1.5');
			cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED)
				.click();
			cy.wait('@PUTdelivery').then(xhr => {
				const methods = xhr.request.body.methods;
				cy.wrap(methods)
					.should('deep.include', {
						type: 'TAQ_PICKUP',
                        currencies: [{currency_code: 'EUR', cost: 1.5}],
						status: 'ACTIVE',
						default: false
					});
			})
		});
	});
});
