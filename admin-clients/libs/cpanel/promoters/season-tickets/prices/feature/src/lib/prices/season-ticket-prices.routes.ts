import { seasonTicketChannelsProviders, SeasonTicketChannelsService } from '@admin-clients/cpanel/promoters/season-tickets/channels/data-access';
import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { RATE_RESTRICTIONS_CHANNELS_SERVICE, RATE_RESTRICTIONS_SERVICE, RateRestrictionsChannelsService } from '@admin-clients/cpanel/promoters/shared/data-access';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { SeasonTicketPriceTypesComponent } from './price-types/season-ticket-price-types.component';
import { SeasonTicketRatesComponent } from './rates/season-ticket-rates.component';
import { SeasonTicketPricesComponent } from './season-ticket-prices.component';
import { SeasonTicketSurchargesComponent } from './surcharges/season-ticket-surcharges.component';

export const SEASON_TICKET_PRICES_ROUTES: Routes = [{
    path: '',
    component: SeasonTicketPricesComponent,
    data: {
        breadcrumb: 'SEASON_TICKET.PRICES'
    },
    children: [
        {
            path: '',
            redirectTo: 'rates',
            pathMatch: 'full'
        },
        {
            path: 'rates',
            component: SeasonTicketRatesComponent,
            data: {
                breadcrumb: 'SEASON_TICKET.RATES_LIST'
            },
            providers: [
                ...seasonTicketChannelsProviders,
                {
                    provide: RATE_RESTRICTIONS_SERVICE,
                    useExisting: SeasonTicketsService
                },
                {
                    provide: RATE_RESTRICTIONS_CHANNELS_SERVICE,
                    useFactory: (seasonTicketChannelsService: SeasonTicketChannelsService): RateRestrictionsChannelsService => ({
                        get$: () => seasonTicketChannelsService.seasonTicketChannelList.getData$(),
                        load: (id: number) => seasonTicketChannelsService.seasonTicketChannelList.load(id, {}),
                        clear: () => seasonTicketChannelsService.seasonTicketChannelList.clear(),
                        channelsPath: ['../../channels']
                    }),
                    deps: [SeasonTicketChannelsService]
                }
            ],
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'price-types',
            component: SeasonTicketPriceTypesComponent,
            data: {
                breadcrumb: 'SEASON_TICKET.PRICE_TYPES.TITLE'
            },
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'surcharges',
            component: SeasonTicketSurchargesComponent,
            data: {
                breadcrumb: 'SEASON_TICKET.SURCHARGES.TITLE'
            },
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'taxes',
            loadComponent: () => import('./taxes/season-ticket-taxes.component').then(m => m.SeasonTicketTaxesComponent),
            data: {
                breadcrumb: 'SEASON_TICKET.TAXES.TITLE'
            },
            canDeactivate: [unsavedChangesGuard()]
        }

    ]
}];
