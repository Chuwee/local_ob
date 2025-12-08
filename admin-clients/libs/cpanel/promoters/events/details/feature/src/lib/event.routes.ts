import { B2B_CONDITIONS_SERVICE, B2bConditionsService } from '@admin-clients/cpanel/b2b/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { inject } from '@angular/core';
import { Routes } from '@angular/router';
import { map } from 'rxjs';
import { EventContainerComponent } from './container/event-container.component';
import { EventDetailsComponent } from './details/event-details.component';

export const EVENT_ROUTES: Routes = [
    {
        path: '',
        component: EventContainerComponent,
        children: [
            {
                path: '',
                component: EventDetailsComponent,
                children: [
                    {
                        path: '',
                        redirectTo: 'general-data',
                        pathMatch: 'full'
                    },
                    {
                        path: 'general-data',
                        loadChildren: () => import('@admin-clients/cpanel/promoters/events/general-data/feature')
                            .then(m => m.EVENT_GENERAL_DATA_ROUTES),
                        data: {
                            breadcrumb: 'EVENTS.GENERAL_DATA'
                        }
                    },
                    {
                        path: 'venue-templates',
                        loadChildren: () => import('@admin-clients/cpanel-promoters-events-venue-templates-feature')
                            .then(r => r.EVENT_VENUE_TEMPLATES_ROUTES),
                        data: {
                            breadcrumb: 'EVENTS.VENUE_TEMPLATES'
                        }
                    },
                    {
                        path: 'elements-info',
                        loadComponent: () => import('@admin-clients/cpanel/promoters/events/feature')
                            .then(m => m.EventElementsInfoComponent),
                        data: {
                            breadcrumb: 'EVENTS.ELEMENTS_INFO.TITLE'
                        }
                    },
                    {
                        path: 'prices',
                        loadChildren: () => import('@admin-clients/cpanel-promoters-events-prices-feature')
                            .then(m => m.EventPricesModule),
                        data: {
                            breadcrumb: 'EVENTS.PRICES'
                        }
                    },
                    {
                        path: 'secondary-market',
                        canDeactivate: [unsavedChangesGuard()],
                        loadChildren: () => import('@admin-clients/cpanel-promoters-events-secondary-market-feature')
                            .then(m => m.EVENT_SECONDARY_MARKET_ROUTES),
                        data: {
                            breadcrumb: 'SECONDARY_MARKET.NAV_TITLE'
                        }
                    },
                    {
                        path: 'sessions',
                        loadChildren: () => import('@admin-clients/cpanel-promoters-events-sessions-feature')
                            .then(m => m.SessionsModule),
                        data: {
                            breadcrumb: 'EVENTS.SESSIONS.TITLE'
                        }
                    },
                    {
                        path: 'session-packs',
                        loadChildren: () => import('@admin-clients/cpanel-promoters-events-session-packs-feature')
                            .then(m => m.SessionPacksModule),
                        data: {
                            breadcrumb: 'EVENTS.SESSION_PACKS'
                        }
                    },
                    {
                        path: 'channels',
                        loadChildren: () => import('@admin-clients/cpanel-promoters-events-channels-feature')
                            .then(m => m.EVENT_CHANNELS_ROUTES),
                        data: {
                            breadcrumb: 'TITLES.CHANNELS'
                        }
                    },
                    {
                        path: 'design',
                        loadChildren: () => import('@admin-clients/cpanel-promoters-events-design-feature')
                            .then(m => m.EVENT_DESIGN_ROUTES),
                        data: {
                            breadcrumb: 'EVENTS.TITLES.DESIGN'
                        }
                    },
                    {
                        path: 'communication',
                        loadChildren: () => import('@admin-clients/cpanel/promoters/events/communication/feature')
                            .then(m => m.EventCommunicationModule),
                        data: {
                            breadcrumb: 'EVENTS.COMMUNICATION.TITLE'
                        }
                    },
                    {
                        path: 'promotions',
                        loadChildren: () => import('@admin-clients/cpanel/promoters/events/promotions/feature')
                            .then(m => m.EVENT_PROMOTION_ROUTES),
                        data: {
                            breadcrumb: 'EVENTS.PROMOTIONS.TITLE'
                        }
                    },
                    {
                        path: 'b2b-conditions',
                        loadChildren: () => import('@admin-clients/cpanel/b2b/feature').then(m => m.promoterConditionsRoutes),
                        providers: [
                            {
                                provide: B2B_CONDITIONS_SERVICE,
                                useFactory: (): B2bConditionsService => {
                                    const eventsSrv = inject(EventsService);
                                    return {
                                        context: 'EVENT',
                                        getContextIdAndCurrency: () => eventsSrv.event.get$().pipe(
                                            map(event => ({ id: event.id, currency: event.currency_code })))
                                    };
                                }
                            }
                        ],
                        data: {
                            breadcrumb: 'EVENTS.B2B_CONDITIONS.TITLE'
                        }
                    }
                ]
            },
            {
                path: 'template-editor',
                loadChildren: () => import('@admin-clients/cpanel-promoters-events-venue-template-editor-feature').then(m => m.routes),
                data: {
                    breadcrumb: 'VENUE_TPLS.TEMPLATE_EDITOR'
                }
            }
        ]
    }
];

