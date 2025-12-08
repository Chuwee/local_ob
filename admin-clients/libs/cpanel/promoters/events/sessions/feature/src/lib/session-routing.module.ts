import { entityUserPermissionCanActivateGuard } from '@admin-clients/cpanel/core/utils';
import { EntityUserPermissions } from '@admin-clients/cpanel/organizations/entity-users/data-access';
import { eventChannelsProviders, EventChannelsService } from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { SessionRateRestrictionsService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import {
    RATE_RESTRICTIONS_CHANNELS_SERVICE, RATE_RESTRICTIONS_SERVICE, RateRestrictionsChannelsService
} from '@admin-clients/cpanel/promoters/shared/data-access';
import { PRESALES_SERVICE } from '@admin-clients/cpanel/shared/data-access';
import { SessionAccessControlComponent } from '@admin-clients/cpanel-promoters-events-sessions-access-control-feature';
import { SessionCommunicationComponent } from '@admin-clients/cpanel-promoters-events-sessions-communication-feature';
import { SessionPlanningComponent } from '@admin-clients/cpanel-promoters-events-sessions-planning-feature';
import { SessionPresalesComponent, SessionPresalesService } from '@admin-clients/cpanel-promoters-events-sessions-presales-feature';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { PrefixPipe } from '@admin-clients/shared/utility/pipes';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SessionAttributesComponent } from './attributes/attributes.component';
import { SessionCapacityComponent } from './capacity/session-capacity.component';
import { SessionCapacityActivityComponent } from './capacity-activity/session-capacity-activity.component';
import { SessionCodeConfigurationComponent } from './code-configuration/session-code-configuration.component';
import { SessionDetailsComponent } from './details/session-details.component';
import { SessionDynamicPricesComponent } from './dynamic-prices/session-dynamic-prices.component';
import { SessionElementsInfoComponent } from './elements-info/session-elements-info.component';
import { SessionOcupationComponent } from './ocupation/session-ocupation.component';

const routes: Routes = [
    {
        path: '',
        component: SessionDetailsComponent,
        children: [
            {
                path: '',
                pathMatch: 'full',
                redirectTo: 'planning'
            },
            {
                path: 'attributes',
                pathMatch: 'full',
                component: SessionAttributesComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'EVENTS.ATTRIBUTES'
                }
            },
            {
                path: 'planning',
                pathMatch: 'full',
                component: SessionPlanningComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'EVENTS.PLANNING'
                }
            },
            {
                path: 'capacity',
                pathMatch: 'full',
                component: SessionCapacityComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'EVENTS.CAPACITY'
                }
            },
            {
                path: 'capacity-activity',
                pathMatch: 'full',
                component: SessionCapacityActivityComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'EVENTS.CAPACITY'
                }
            },
            {
                path: 'elements-info',
                pathMatch: 'full',
                component: SessionElementsInfoComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'EVENTS.ELEMENTS_INFO.SESSIONS_TITLE'
                }
            },
            {
                path: 'presales',
                pathMatch: 'full',
                component: SessionPresalesComponent,
                canDeactivate: [unsavedChangesGuard()],
                providers: [
                    {
                        provide: PRESALES_SERVICE,
                        useClass: SessionPresalesService
                    },
                    PrefixPipe.provider('EVENTS.SESSION.')
                ],
                data: {
                    breadcrumb: 'EVENTS.PRESALES'
                }
            },
            {
                path: 'seat-status',
                pathMatch: 'full',
                component: SessionOcupationComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'EVENTS.OCUPATION.TITLE',
                    type: 'session'
                }
            },
            {
                path: 'communication',
                pathMatch: 'full',
                component: SessionCommunicationComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'EVENTS.COMMUNICATION.TITLE'
                }
            },
            {
                path: 'dynamic-prices',
                pathMatch: 'full',
                component: SessionDynamicPricesComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'EVENTS.DYNAMIC_PRICES.TITLE'
                }
            },
            {
                path: 'access-control',
                pathMatch: 'full',
                component: SessionAccessControlComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'EVENTS.ACCESS_CONTROL'
                }
            },
            {
                path: 'secondary-market',
                pathMatch: 'full',
                loadComponent: () =>
                    import('@admin-clients/cpanel-promoters-events-sessions-secondary-market-feature')
                        .then(c => c.SessionSecondaryMarketComponent),
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'EVENTS.SESSION.SECONDARY_MARKET'
                }
            },
            {
                path: 'other-settings',
                pathMatch: 'full',
                loadComponent: () => import('@admin-clients/cpanel-promoters-events-sessions-other-settings-feature')
                    .then(c => c.SessionOtherSettingsComponent),
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'EVENTS.OTHER_SETTINGS'
                },
                providers: [
                    PrefixPipe.provider('EVENTS.'),
                    ...eventChannelsProviders,
                    {
                        provide: RATE_RESTRICTIONS_SERVICE,
                        useClass: SessionRateRestrictionsService
                    },
                    {
                        provide: RATE_RESTRICTIONS_CHANNELS_SERVICE,
                        useFactory: (eventChannelsService: EventChannelsService): RateRestrictionsChannelsService => ({
                            get$: () => eventChannelsService.eventChannelsList.getData$(),
                            load: (id: number) => eventChannelsService.eventChannelsList.load(id, {}),
                            clear: () => eventChannelsService.eventChannelsList.clear(),
                            channelsPath: ['../../../channels']
                        }),
                        deps: [EventChannelsService]
                    }
                ]
            },
            {
                path: 'automatic-sales',
                pathMatch: 'full',
                loadComponent: () =>
                    import('@admin-clients/cpanel/promoters/events/automatic-sales')
                        .then(c => c.AutomaticSalesComponent),
                canDeactivate: [unsavedChangesGuard()],
                canActivate: [entityUserPermissionCanActivateGuard],
                data: {
                    permissions: [EntityUserPermissions.automaticSales],
                    breadcrumb: 'EVENTS.SESSION.AUTOMATIC_SALES.TITLE'
                }
            },
            {
                path: 'code-configuration',
                pathMatch: 'full',
                component: SessionCodeConfigurationComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'EVENTS.SESSION.CONF_CODES'
                }
            }
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class SessionRoutingModule {
}
