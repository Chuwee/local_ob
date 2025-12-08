import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { EventSecondaryMarketComponent } from './event-secondary-market.component';

export const EVENT_SECONDARY_MARKET_ROUTES: Routes = [
    {
        path: '',
        component: EventSecondaryMarketComponent,
        data: {
            breadcrumb: 'EVENTS.SECONDARY_MARKET.TITLE'
        },
        children: [
            {
                path: '',
                redirectTo: 'listing-config',
                pathMatch: 'full'
            },
            {
                path: 'listing-config',
                loadComponent: () => import('./listing-config/events-secondary-market-listing-config.component')
                    .then(m => m.EventsSecondaryMarketListingConfigComponent),
                data: {
                    breadcrumb: 'EVENTS.SECONDARY_MARKET.SELL_CONFIG.TITLE '
                },
                canDeactivate: [unsavedChangesGuard()]
            },
            {
                path: 'buy-config',
                loadComponent: () => import('./buy-config/event-secondary-market-buy-config.component')
                    .then(m => m.EventSecondaryMarketBuyConfigComponent),
                data: {
                    breadcrumb: 'EVENTS.SECONDARY_MARKET.BUY_CONFIG.TITLE'
                },
                canDeactivate: [unsavedChangesGuard()]
            }
        ]
    }
];
