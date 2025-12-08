import { entityUserPermissionCanActivateGuard } from '@admin-clients/cpanel/core/utils';
import { EntityUserPermissions } from '@admin-clients/cpanel/organizations/entity-users/data-access';
import { eventChannelsProviders, EventChannelsService } from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { SessionRateRestrictionsService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { RATE_RESTRICTIONS_CHANNELS_SERVICE, RATE_RESTRICTIONS_SERVICE, RateRestrictionsChannelsService } from '@admin-clients/cpanel/promoters/shared/data-access';
import { PRESALES_SERVICE } from '@admin-clients/cpanel/shared/data-access';
import { SessionAccessControlComponent } from '@admin-clients/cpanel-promoters-events-sessions-access-control-feature';
import { SessionCommunicationComponent } from '@admin-clients/cpanel-promoters-events-sessions-communication-feature';
import {
    SessionCapacityComponent, SessionCodeConfigurationComponent, SessionDynamicPricesComponent, SessionOcupationComponent
} from '@admin-clients/cpanel-promoters-events-sessions-feature';
import { SessionPlanningComponent } from '@admin-clients/cpanel-promoters-events-sessions-planning-feature';
import { SessionPresalesComponent, SessionPresalesService } from '@admin-clients/cpanel-promoters-events-sessions-presales-feature';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { PrefixPipe } from '@admin-clients/shared/utility/pipes';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SessionPacksContainerComponent } from './container/session-packs-container.component';
import { SessionPackDetailsComponent } from './session-pack/details/session-pack-details.component';
import { SessionPackRefundsComponent } from './session-pack/refunds/session-pack-refunds.component';

const routes: Routes = [
    {
        path: '',
        component: SessionPacksContainerComponent,
        children: [
            {
                path: '',
                component: null,
                pathMatch: 'full',
                children: []
            },
            {
                path: ':sessionId',
                component: SessionPackDetailsComponent,
                data: {
                    breadcrumb: 'SESSION_PACK_EDITOR'
                },
                children: [
                    {
                        path: '',
                        pathMatch: 'full',
                        redirectTo: 'planning'
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
                        path: 'presales',
                        pathMatch: 'full',
                        component: SessionPresalesComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        data: {
                            breadcrumb: 'EVENTS.PRESALES'
                        },
                        providers: [
                            {
                                provide: PRESALES_SERVICE,
                                useClass: SessionPresalesService
                            },
                            PrefixPipe.provider('EVENTS.SESSION.')
                        ]
                    },
                    {
                        path: 'seat-status',
                        pathMatch: 'full',
                        component: SessionOcupationComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        data: {
                            breadcrumb: 'EVENTS.OCUPATION.TITLE',
                            type: 'sessionPack'
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
                        path: 'other-settings',
                        pathMatch: 'full',
                        loadComponent: () =>
                            import('@admin-clients/cpanel-promoters-events-sessions-other-settings-feature')
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
                        path: 'refunds',
                        pathMatch: 'full',
                        component: SessionPackRefundsComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        data: {
                            breadcrumb: 'EVENTS.REFUNDS'
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
                    }
                ]
            }
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class SessionPacksRoutingModule { }
