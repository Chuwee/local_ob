import elements from './season-tickets-channels-components';

describe('Tests of Season tickets/channel list', () => {
    context('using event standardSeasonTicket of event entity and user operAdminQaa', () => {
        let seasonTicket;
        before(() => {
            cy.fixture('events').then((events) => { seasonTicket = events.standardSeasonTicket })
        });
        beforeEach(() => {
            cy.login('operAdminQaa');
            const urlRegExp = new RegExp(`(/season-tickets/${seasonTicket.id}/channels\\?)(?!branch|sp).`);
            cy.intercept('GET', urlRegExp).as('getChannelsInfo');
            cy.visit(`/season-tickets/${seasonTicket.id}/channels`);
        });
        it('the channels-list shows all season-ticket channels sorted alphabetically and the first is selected', () => {
            cy.wait('@getChannelsInfo', { timeout: 10000 }).then((xhr) => {
                expect(xhr.request.url).to.contain('sort=name%3Aasc');
                const channelList = xhr.response.body.data;
                channelList.forEach((item) => {
                    cy.get(`#channel-list-option-${item.channel.id}`);
                });
                cy.get(`#channel-list-option-${channelList[0].channel.id}`).should(
                    'have.class',
                    'mdc-list-item--selected'
                );
                cy.get(elements.CHANNELS_LIST_TOTAL).should('contain', channelList.length);
            });
        });
        it('the channel-list shows all info related to that channel', () => {
            cy.wait('@getChannelsInfo').then((xhr) => {
                const firstChannel = xhr.response.body.data[0];
                const releaseDate = firstChannel.settings.release.date
                    .split('T')[0]
                    .split('-')
                    .reverse()
                    .join('/');
                const saleDate = firstChannel.settings.sale.start_date
                    .split('T')[0]
                    .split('-')
                    .reverse()
                    .join('/');
                cy.get(`#channel-list-option-${firstChannel.channel.id}`)
                    .should('contain', firstChannel.channel.name)
                    .should('contain', firstChannel.channel.entity.name)
                    .should('contain', releaseDate)
                    .should('contain', saleDate);
                const releaseStatus = firstChannel.status.release;
                const saleStatus = firstChannel.status.sale;
                let releaseClass,
                    saleClass;
                switch (releaseStatus) {
                    case 'RELEASED':
                        releaseClass = 'success'
                        break;
                    case 'PENDING_RELATIONSHIP':
                        releaseClass = 'pending'
                        break;
                    case 'RELEASE_FINISHED':
                        releaseClass = 'finished'
                        break;
                }
                cy.get(elements.CHANNEL_STATUS)
                    .contains('P')
                    .should('have.class', releaseClass);
                switch (saleStatus) {
                    case 'SALE_PENDING':
                        saleClass = 'pending'
                        break;
                    case 'PENDING_RELATIONSHIP':
                        saleClass = 'pending'
                        break;
                    case 'RELEASE_FINISHED':
                        saleClass = 'finished'
                        break;
                }
                cy.get(elements.CHANNEL_STATUS)
                    .contains('V')
                    .should('have.class', saleClass);
                // TO_DO: stub /channels response to simplify asserts
            });
        });
        it('navigates when selecting a different channel in the channel-list', () => {
            cy.wait('@getChannelsInfo').then(xhr => {
                const items = xhr.response.body.data;
                cy.url().should('contain', items[0].channel.id);
                cy.intercept('GET', `**/season-tickets/${items[0].season_ticket.id}/channels/*`).as('GETchannel');
                cy.get(elements.CHANNEL_LIST_ITEM).eq(1).click();
                cy.wait('@GETchannel').then(xhr => {
                    expect(xhr.request.url).to.contain(
                        `/channels/${items[1].channel.id}`
                    )
                })
            })
        });
        it('clicks the add-channel button to open modal with available channel list and filters', () => {
            cy.intercept('GET', '**/mgmt-api/v1/channels?*').as('GETchannels');
            cy.get(elements.ADD_CHANNEL_BUTTON).click();
            cy.wait('@GETchannels').then((xhr) => {
                expect(xhr.request.url).to.contain(
                    '&include_third_party_channels=true'
                );
                cy.get(elements.NEW_CH_GRID_ITEM).should(
                    'have.length',
                    xhr.response.body.data.length
                );
            });
            cy.get(elements.NEW_CH_FILTER_TYPE).click();
            cy.get(elements.NEW_CH_FILTER_OPTS)
                .should('have.length', 8)
                .first()
                .click();
            cy.get(elements.NEW_CH_FILTER_OPTS).should('not.be.visible');
            cy.get(elements.NEW_CH_CLOSE_MODAL).click();
            cy.get(elements.ADD_CHANNEL_BUTTON).click();
            cy.get(elements.NEW_CH_ACCEPT_BUTTON).should('be.visible');
            cy.get(elements.NEW_CH_CANCEL_BUTTON).click();
        });
        it('uses filters when adding a channel to the event', () => {
            cy.intercept('GET', '**/mgmt-api/v1/channels?*&type=WEB*').as(
                'GETchannels'
            );
            cy.get(elements.ADD_CHANNEL_BUTTON).click();
            cy.get(elements.NEW_CH_FILTER_TYPE).click();
            cy.get(elements.NEW_CH_FILTER_OPTS).contains('OB Portal').click();
            cy.wait('@GETchannels').then((xhr) => {
                expect(xhr.request.url).to.contain(
                    '&type=WEB&include_third_party_channels=true'
                );
            });
            cy.get(elements.NEW_CH_FILTER_NAME).type('ecv{enter}');
            cy.wait('@GETchannels').then((xhr) => {
                expect(xhr.request.url).to.contain(
                    '&name=ecv&type=WEB&include_third_party_channels=true'
                );
                const channelList = xhr.response.body.data;
                channelList.forEach((chan) => {
                    assert.match(chan.name, /ecv/i);
                });
            });
        });
    })
})
