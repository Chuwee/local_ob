import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { SeasonTicketAdditionalOptionsComponent } from './additional-options/season-ticket-additional-options.component';
import { SeasonTicketPrincipalInfoComponent } from './principal-info/season-ticket-principal-info.component';
import { SeasonTicketRenewalsConfigComponent } from './renewals-config/season-ticket-renewals-config.component';
import { SeasonTicketGeneralDataComponent } from './season-ticket-general-data.component';

export const SEASON_TICKET_GEN_DATA_ROUTES: Routes = [
    {
        path: '',
        component: SeasonTicketGeneralDataComponent,
        data: {
            breadcrumb: 'SEASON_TICKET.GENERAL_DATA'
        },
        children: [
            {
                path: '',
                redirectTo: 'principal-info',
                pathMatch: 'full'
            },
            {
                path: 'principal-info',
                component: SeasonTicketPrincipalInfoComponent,
                data: {
                    breadcrumb: 'SEASON_TICKET.PRINCIPAL_INFO'
                },
                canDeactivate: [unsavedChangesGuard()]
            },
            {
                path: 'additional-options',
                component: SeasonTicketAdditionalOptionsComponent,
                data: {
                    breadcrumb: 'SEASON_TICKET.ADDITIONAL_OPTIONS'
                },
                canDeactivate: [unsavedChangesGuard()]
            },
            {
                path: 'renewals-config',
                component: SeasonTicketRenewalsConfigComponent,
                data: {
                    breadcrumb: 'SEASON_TICKET.RENEWALS_CONFIG'
                },
                canDeactivate: [unsavedChangesGuard()]
            }
        ]
    }
];
