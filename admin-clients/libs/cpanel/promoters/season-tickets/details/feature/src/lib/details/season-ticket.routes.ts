import { B2B_CONDITIONS_SERVICE, B2bConditionsService } from '@admin-clients/cpanel/b2b/data-access';
import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { inject } from '@angular/core';
import { Routes } from '@angular/router';
import { map } from 'rxjs';
import { SeasonTicketDetailsComponent } from './season-ticket-details.component';
import {
    seasonTicketGenerationStatusErrorCanActivateGuard, seasonTicketGenerationStatusErrorCanMatchGuard
} from './season-ticket-generation-status-error.guard';

export const SEASON_TICKET_ROUTES: Routes = [
    {
        path: '',
        component: SeasonTicketDetailsComponent,
        children: [
            {
                path: '',
                redirectTo: 'general-data',
                pathMatch: 'full'
            },
            {
                path: 'general-data',
                loadChildren: () => import('@admin-clients/cpanel/promoters/season-tickets/general-data/feature')
                    .then(m => m.SEASON_TICKET_GEN_DATA_ROUTES),
                data: {
                    breadcrumb: 'SEASON_TICKET.GENERAL_DATA.TITLE'
                }
            },
            {
                path: 'communication',
                loadChildren: () => import('@admin-clients/cpanel-promoters-season-tickets-communication-feature')
                    .then(m => m.SeasonTicketCommunicationModule),
                data: {
                    breadcrumb: 'SEASON_TICKET.COMMUNICATION.TITLE'
                }
            },
            {
                path: 'channels',
                loadChildren: () => import('@admin-clients/cpanel-promoters-season-tickets-channels-feature')
                    .then(m => m.SEASON_TICKET_CHANNELS_ROUTES),
                data: {
                    breadcrumb: 'TITLES.CHANNELS'
                },
                canMatch: [seasonTicketGenerationStatusErrorCanMatchGuard],
                canActivate: [
                    seasonTicketGenerationStatusErrorCanActivateGuard
                ]
            },
            {
                path: 'operative',
                loadChildren: () => import('@admin-clients/cpanel-promoters-season-tickets-operative-feature')
                    .then(c => c.SEASON_TICKET_OPERATIVE_ROUTES),
                data: {
                    breadcrumb: 'SEASON_TICKET.OPERATIVE.TITLE'
                },
                canActivate: [
                    seasonTicketGenerationStatusErrorCanActivateGuard
                ],
                canDeactivate: [unsavedChangesGuard()]
            },
            {
                path: 'capacity',
                loadChildren: () => import('@admin-clients/cpanel-promoters-season-tickets-capacity-feature')
                    .then(m => m.SeasonTicketCapacityModule),
                data: {
                    breadcrumb: 'SEASON_TICKET.CAPACITY.TITLE'
                },
                canMatch: [seasonTicketGenerationStatusErrorCanMatchGuard],
                canActivate: [
                    seasonTicketGenerationStatusErrorCanActivateGuard
                ]
            },
            {
                path: 'prices',
                loadChildren: () => import('@admin-clients/cpanel-promoters-season-tickets-prices-feature')
                    .then(m => m.SEASON_TICKET_PRICES_ROUTES),
                data: {
                    breadcrumb: 'SEASON_TICKET.PRICES'
                },
                canDeactivate: [unsavedChangesGuard()],
                canActivate: [
                    seasonTicketGenerationStatusErrorCanActivateGuard
                ]
            },
            {
                path: 'sessions',
                loadChildren: () => import('@admin-clients/cpanel-promoters-season-tickets-sessions-list-feature')
                    .then(m => m.SeasonTicketSessionsModule),
                data: {
                    breadcrumb: 'SEASON_TICKET.SESSIONS'
                },
                canMatch: [seasonTicketGenerationStatusErrorCanMatchGuard],
                canActivate: [
                    seasonTicketGenerationStatusErrorCanActivateGuard
                ]
            },
            {
                path: 'renewals',
                loadChildren: () => import('@admin-clients/cpanel-promoters-season-tickets-renewals-feature')
                    .then(m => m.SeasonTicketsRenewalsModule),
                data: {
                    breadcrumb: 'SEASON_TICKET.RENEWALS.TITLE'
                },
                canMatch: [seasonTicketGenerationStatusErrorCanMatchGuard],
                canActivate: [
                    seasonTicketGenerationStatusErrorCanActivateGuard
                ]
            },
            {
                path: 'locality-management',
                loadChildren: () => import('@admin-clients/cpanel-promoters-season-tickets-locality-management-feature')
                    .then(m => m.SeasonTicketLocalityManagementModule),
                data: {
                    breadcrumb: 'SEASON_TICKET.LOCALITY_MANAGEMENT.TITLE'
                },
                canActivate: [
                    seasonTicketGenerationStatusErrorCanActivateGuard
                ],
                canDeactivate: [unsavedChangesGuard()]
            },
            {
                path: 'promotions',
                loadChildren: () => import('@admin-clients/cpanel/promoters/season-tickets/promotions/feature')
                    .then(m => m.SEASON_TICKET_PROMOTIONS_ROUTES),
                data: {
                    breadcrumb: 'SEASON_TICKET.PROMOTIONS.TITLE'
                },
                canMatch: [seasonTicketGenerationStatusErrorCanMatchGuard],
                canActivate: [
                    seasonTicketGenerationStatusErrorCanActivateGuard
                ]
            },
            {
                path: 'b2b-conditions',
                loadChildren: () => import('@admin-clients/cpanel/b2b/feature').then(m => m.promoterConditionsRoutes),
                providers: [
                    {
                        provide: B2B_CONDITIONS_SERVICE,
                        useFactory: (): B2bConditionsService => {
                            const seasonTicketSrv = inject(SeasonTicketsService);
                            return {
                                context: 'SEASON_TICKET',
                                getContextIdAndCurrency: () => seasonTicketSrv.seasonTicket.get$().pipe(
                                    map(st => ({ id: st.id, currency: st.currency_code }))
                                )
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
        loadChildren: () => import('@admin-clients/cpanel-promoters-season-tickets-venue-template-editor-feature')
            .then(m => m.SeasonTicketTemplateEditorModule),
        data: {
            breadcrumb: 'VENUE_TPLS.TEMPLATE_EDITOR'
        }
    }
];
