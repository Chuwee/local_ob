import { ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { PromotersProfessionalSellingComponent } from '@admin-clients/cpanel-promoters-shared-professional-selling';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { SeasonTicketChannelCommissionsComponent } from './commissions/season-ticket-channel-commissions.component';
import { SeasonTicketChannelsContainerComponent } from './container/season-ticket-channels-container.component';
import { SeasonTicketChannelChannelContentComponent } from './content/season-ticket-channel-channel-content.component';
import { SeasonTicketChannelDetailsComponent } from './details/season-ticket-channel-details.component';
import { SeasonTicketChannelGeneralDataComponent } from './general-data/season-ticket-channel-general-data.component';
import { SeasonTicketChannelSurchargesComponent } from './surcharges/season-ticket-channel-surcharges.component';

export const SEASON_TICKET_CHANNELS_ROUTES: Routes = [
    {
        path: '',
        component: SeasonTicketChannelsContainerComponent,
        children: [
            {
                path: '',
                component: null,
                pathMatch: 'full',
                children: []
            },
            {
                path: ':channelId',
                component: SeasonTicketChannelDetailsComponent,
                data: {
                    breadcrumb: 'CHANNEL_EDITOR'
                },
                children: [
                    {
                        path: '',
                        pathMatch: 'full',
                        redirectTo: 'general-data'
                    },
                    {
                        path: 'general-data',
                        pathMatch: 'full',
                        component: SeasonTicketChannelGeneralDataComponent,
                        data: {
                            breadcrumb: 'CHANNELS.GENERAL_DATA'
                        },
                        canDeactivate: [unsavedChangesGuard()]
                    },
                    {
                        path: 'professional-selling',
                        pathMatch: 'full',
                        component: PromotersProfessionalSellingComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        data: {
                            breadcrumb: 'CHANNELS.PROFESSIONAL_SELLING.TITLE',
                            allowedChannelTypes: [ChannelType.webB2B] // can't use in a resolver due to state-machine so used there :(
                        }
                    },
                    {
                        path: 'surcharges',
                        pathMatch: 'full',
                        component: SeasonTicketChannelSurchargesComponent,
                        data: {
                            breadcrumb: 'CHANNELS.SURCHARGES.TITLE'
                        },
                        canDeactivate: [unsavedChangesGuard()]
                    },
                    {
                        path: 'channel-commission',
                        pathMatch: 'full',
                        component: SeasonTicketChannelCommissionsComponent,
                        data: {
                            breadcrumb: 'CHANNELS.COMMISSIONS.TITLE'
                        }
                    },
                    {
                        path: 'contents',
                        pathMatch: 'full',
                        component: SeasonTicketChannelChannelContentComponent,
                        data: {
                            breadcrumb: 'CHANNELS.CONTENTS'
                        }
                    }
                ]
            }
        ]
    }
];
