import elements from './event-channels-components';
import $dialogs from '../../../shared/components/dialogs-components';

describe('Tests of Event/channel list', () => {
    context('using event SimpleEvent of event entity and user operAdminCy', () => {
        let event;
        let routeRegex;
        before(() => {
            cy.fixture('events').then((events) => {
                event = events.eventSimpleEvent;
            })
        });
        beforeEach(() => {
            cy.login('operAdminCy');
            routeRegex = new RegExp(`(/events/${event.id}/channels\\?)(?!branch|sp).`);
            cy.intercept(routeRegex).as('GETevent/channels');
            cy.visit(`/events/${event.id}/channels`);
        });
        it('the channels-list shows all event-channels sorted alphabetically and the first is selected', () => {
            cy.wait('@GETevent/channels').then((xhr) => {
                expect(xhr.request.url).to.contain('sort=name%3Aasc');
                const channelList = xhr.response.body.data;
                channelList.forEach((chan) => {
                    cy.get(`#channel-list-option-${chan.channel.id}`);
                });
                cy.get(`#channel-list-option-${channelList[0].channel.id}`).should(
                    'have.class',
                    'mdc-list-item--selected'
                );
                cy.get(elements.CHANNEL_LIST_TOTAL).should(
                    'contain',
                    channelList.length
                );
            });
        });
        it('the channel-list shows all info related to that channel', () => {
            cy.intercept(routeRegex, { fixture: 'channels/getChannels' }).as('GETevent/channels');
            cy.wait('@GETevent/channels').then(xhr => {
                const channel = xhr.response.body.data[0];
                cy.get(`#channel-list-option-${channel.channel.id}`)
                    .should('contain', channel.channel.name)
                    .should('contain', channel.channel.entity.name)
                    .should('contain', '25/02/2020')
                    .should('contain', '25/02/2020');
                cy.contains(elements.CHANNEL_LIST_ITEM_BADGE, 'P')
                    .should('have.class', 'success');
                cy.contains(elements.CHANNEL_LIST_ITEM_BADGE, 'V')
                    .should('have.class', 'success');
            });
        });
        it('navigates when selecting a different channel in the channel-list', () => {
            cy.wait('@GETevent/channels').then(xhr => {
                const channels = xhr.response.body.data;
                cy.url().should('contain', channels[0].channel.id);
                cy.intercept('GET', `**/events/${channels[0].event.id}/channels/*`).as('GETchannel');
                cy.get(elements.CHANNEL_LIST_ITEM).eq(1).click();
                cy.wait('@GETchannel').then(xhr => {
                    expect(xhr.request.url).to.contain(
                        `/channels/${channels[1].channel.id}`
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
        it('hides the channel-list and reopens it again', () => {
            cy.get(elements.CHANNEL_LIST_TOTAL).should('be.visible');
            cy.get(elements.CHANNEL_LIST_SIDEBAR_TOGGLE).click();
            cy.get(elements.CHANNEL_LIST_TOTAL).should('not.be.visible');
            cy.get(elements.CHANNEL_LIST_SIDEBAR_TOGGLE).click();
            cy.get(elements.CHANNEL_LIST_TOTAL).should('be.visible');
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
        it('adds a new channel to the event', () => {
            cy.intercept('GET', '**/mgmt-api/v1/channels?*').as('GETchannels');
            cy.get(elements.ADD_CHANNEL_BUTTON).click();
            cy.wait('@GETchannels').then((getChan) => {
                const firstChannel = getChan.response.body.data[0];
                cy.fixture('events/getAfterAddChannel').then((fixt) => {
                    fixt.data[0].channel = firstChannel;
                    cy.get(elements.NEW_CH_GRID_ITEM).not('.mat-list-item-disabled').first().click();
                    cy.get(elements.NEW_CH_GRID_ITEM)
                        .should('have.attr', 'aria-selected', 'true');
                    cy.intercept('GET', '**/events/*/channels?**', (req) => {
                        req.reply({ body: fixt });
                    }).as('STUBchannels');
                });
                cy.intercept('POST', '**/channels*', { statusCode: 201 }).as(
                    'POSTchannels'
                );
                cy.intercept('GET', '**/events/*/channels/*', {}).as('STUBeventChannel');
                cy.get(elements.NEW_CH_ACCEPT_BUTTON)
                    .should('not.have.class', 'mat-button-disabled')
                    .click();
                cy.wait('@POSTchannels').then((xhr) => {
                    expect(xhr.request.body).to.have.key('channel_id');
                });
                cy.get('.snackbar-content').should('be.visible');
            });
        });
        it('deletes a channel from the event', () => {
            cy.intercept('DELETE', '**/events/*/channels/**', {
                statusCode: 204,
            }).as('DELchannel');
            cy.get(elements.DELETE_CHANNEL_BUTTON).click();
            cy.getLiteral('EVENTS.CHANNEL.DELETE_EVENT_CHANNEL').then((literal) => {
                cy.get($dialogs.DIALOG_TITLE_TEXT).should('have.text', literal);
                cy.url().then((url) => {
                    const subStr = url.split('/');
                    const channelId = subStr[subStr.length - 2];
                    cy.get($dialogs.ALERT_BUTTON_CONFIRM).click();
                    cy.wait('@DELchannel').then((xhr) => {
                        expect(xhr.request.url).to.contain(
                            `/channels/${channelId}`
                        );
                    });
                    cy.get('.snackbar-content').should('be.visible');
                });
            });
        });
        it('cancels deletion of a channel from the event', () => {
            cy.intercept('DELETE', '**/events/*/channels/**', {
                statusCode: 204,
            }).as('DELchannel');
            cy.get(elements.DELETE_CHANNEL_BUTTON).click();
            cy.getLiteral('EVENTS.CHANNEL.DELETE_EVENT_CHANNEL').then((literal) => {
                cy.get($dialogs.DIALOG_TITLE_TEXT).should('have.text', literal);
                cy.get($dialogs.ALERT_BUTTON_CANCEL).click();
                cy.get($dialogs.DIALOG_TITLE).should('not.exist');
            })
        })
    })
})
