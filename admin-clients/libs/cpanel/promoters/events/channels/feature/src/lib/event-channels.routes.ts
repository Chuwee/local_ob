import { ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { PromotersProfessionalSellingComponent } from '@admin-clients/cpanel-promoters-shared-professional-selling';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { EventChannelCommissionsComponent } from './commissions/event-channel-commissions.component';
import { EventChannelsContainerComponent } from './container/event-channels-container.component';
import { EventChannelChannelContentComponent } from './content/event-channel-channel-content.component';
import { EventChannelDetailsComponent } from './details/event-channel-details.component';
import { EventChannelGeneralDataComponent } from './general-data/event-channel-general-data.component';
import { EventChannelImagesComponent } from './images/event-channel-images.component';
import { EventChannelSurchargesComponent } from './surcharges/event-channel-surcharges.component';
import { EventChannelDestinationComponent } from './destination/event-channel-destination.component';

export const EVENT_CHANNELS_ROUTES: Routes = [
    {
        path: '',
        component: EventChannelsContainerComponent,
        children: [
            {
                path: '',
                component: null,
                pathMatch: 'full',
                children: []
            },
            {
                path: ':channelId',
                component: EventChannelDetailsComponent,
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
                        component: EventChannelGeneralDataComponent,
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
                        path: 'publish',
                        pathMatch: 'full',
                        loadComponent: () => import('./publish/event-channel-publish.component'),
                        canDeactivate: [unsavedChangesGuard()],
                        data: {
                            breadcrumb: 'CHANNELS.PROFESSIONAL_SELLING.TITLE',
                            allowedChannelTypes: [ChannelType.webB2B] // can't use in a resolver due to state-machine so used there :(
                        }
                    },
                    {
                        path: 'surcharges',
                        pathMatch: 'full',
                        component: EventChannelSurchargesComponent,
                        data: {
                            breadcrumb: 'CHANNELS.SURCHARGES.TITLE'
                        },
                        canDeactivate: [unsavedChangesGuard()]
                    },
                    {
                        path: 'channel-commission',
                        pathMatch: 'full',
                        component: EventChannelCommissionsComponent,
                        data: {
                            breadcrumb: 'CHANNELS.COMMISSIONS.TITLE'
                        }
                    },
                    {
                        path: 'images',
                        pathMatch: 'full',
                        component: EventChannelImagesComponent,
                        data: {
                            breadcrumb: 'EVENTS.CHANNEL.SQUARE_IMAGES.TITLE'
                        },
                        canDeactivate: [unsavedChangesGuard()]
                    },
                    {
                        path: 'contents',
                        pathMatch: 'full',
                        component: EventChannelChannelContentComponent,
                        data: {
                            breadcrumb: 'CHANNELS.CONTENTS'
                        }
                    },
                    {
                        path: 'destination',
                        pathMatch: 'full',
                        component: EventChannelDestinationComponent,
                        data: {
                            breadcrumb: 'CHANNELS.DESTINATION_OPTIONS'
                        }
                    }
                ]
            }
        ]
    }
];
