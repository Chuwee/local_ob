import { SeasonTicketTransferSeatsComponent }
    from '@admin-clients/cpanel/promoters/season-tickets/locality-management/transfer-seats/feature';
import { SeasonTicketChangeSeatsComponent } from '@admin-clients/cpanel-promoters-season-tickets-locality-management-change-seats-feature';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SeasonTicketLocalityManagementComponent } from './season-ticket-locality-management.component';
import {
    ReleaseSeatsConfigurationComponent, ReleaseSeatsReleasedListComponent
} from '@admin-clients/cpanel/promoters/season-tickets/locality-management/release-seats/feature';

const routes: Routes = [{
    path: '',
    component: SeasonTicketLocalityManagementComponent,
    data: {
        breadcrumb: 'SEASON_TICKET.LOCALITY_MANAGEMENT'
    },
    children: [
        {
            path: '',
            redirectTo: 'change-seats',
            pathMatch: 'full'
        },
        {
            path: 'change-seats',
            component: SeasonTicketChangeSeatsComponent,
            data: {
                breadcrumb: 'SEASON_TICKET.CHANGE_SEAT.TITLE'
            },
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'transfer-seats',
            component: SeasonTicketTransferSeatsComponent,
            data: {
                breadcrumb: 'SEASON_TICKET.TRANSFER_SEAT.TITLE'
            },
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'release-seats/config',
            component: ReleaseSeatsConfigurationComponent,
            data: {
                breadcrumb: 'SEASON_TICKET.RELEASE_SEAT.CONFIG.TITLE'
            },
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'release-seats/released-list',
            component: ReleaseSeatsReleasedListComponent,
            data: {
                breadcrumb: 'SEASON_TICKET.RELEASE_SEAT.RELEASED_LIST.TITLE'
            }
        },
        {
            path: 'secondary-market',
            loadComponent: () => import('@admin-clients/cpanel-promoters-season-tickets-secondary-market-feature')
                .then(m => m.SeasonTicketsSecondaryMarketComponent),
            data: {
                breadcrumb: 'SEASON_TICKET.SECONDARY_MARKET.TITLE'
            },
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'ticket-redemption',
            loadComponent: () => import('@admin-clients/cpanel/promoters/season-tickets/locality-management/ticket-redemption/feature')
                .then(m => m.SeasonTicketTicketRedemptionComponent),
            data: {
                breadcrumb: 'SEASON_TICKET.TICKET_REDEMPTION.TITLE'
            },
            canDeactivate: [unsavedChangesGuard()]
        }
    ]
}];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class SeasonTicketLocalityManagementRoutingModule {
}
