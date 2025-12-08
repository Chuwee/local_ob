import { PRESALES_SERVICE } from '@admin-clients/cpanel/shared/data-access';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { PrefixPipe } from '@admin-clients/shared/utility/pipes';
import { Routes } from '@angular/router';
import { SeasonTicketPresalesService } from './presales/season-ticket-presale-service';
import { SeasonTicketOperativeComponent } from './season-ticket-operative.component';

export const SEASON_TICKET_OPERATIVE_ROUTES: Routes = [
    {
        path: '',
        component: SeasonTicketOperativeComponent,
        data: {
            breadcrumb: 'SEASON_TICKET.OPERATIVE.TITLE'
        },
        children: [
            {
                path: '',
                redirectTo: 'planning',
                pathMatch: 'full'
            },
            {
                path: 'planning',
                loadComponent: () => import('./planning/season-ticket-operative-planning.component')
                    .then(m => m.SeasonTicketOperativePlanningComponent),
                data: {
                    breadcrumb: 'SEASON_TICKET.OPERATIVE.PLANNING.TITLE'
                },
                canDeactivate: [unsavedChangesGuard()]
            },
            {
                path: 'presales',
                loadComponent: () => import('./presales/season-ticket-operative-presales.component')
                    .then(m => m.SeasonTicketOperativePresalesComponent),
                data: {
                    breadcrumb: 'SEASON_TICKET.OPERATIVE.PRESALES.TITLE'
                },
                providers: [
                    {
                        provide: PRESALES_SERVICE,
                        useClass: SeasonTicketPresalesService
                    },
                    PrefixPipe.provider('SEASON_TICKET.')
                ],
                canDeactivate: [unsavedChangesGuard()]
            },
            {
                path: 'options',
                loadComponent: () => import('./options/season-ticket-operative-options.component')
                    .then(m => m.SeasonTicketOperativeOptionsComponent),
                data: {
                    breadcrumb: 'SEASON_TICKET.OPERATIVE.OPTIONS.TITLE'
                },
                canDeactivate: [unsavedChangesGuard()]
            }
        ]
    }
];