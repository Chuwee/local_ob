import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { PackPricesContainerComponent } from './pack-prices-container.component';
import { PackPriceZonesComponent } from './price-zones/pack-price-zones.component';
import { PackPricesComponent } from './prices/pack-prices.component';

export const PACK_PRICES_ROUTES: Routes = [
    {
        path: '',
        component: PackPricesContainerComponent,
        children: [
            {
                path: '',
                redirectTo: 'pack-prices',
                pathMatch: 'full'
            },
            {
                path: 'pack-prices',
                component: PackPricesComponent,
                data: {
                    breadcrumb: 'PACK.PRICES.TITLE'
                },
                canDeactivate: [unsavedChangesGuard()]
            },
            {
                path: 'pack-price-zones',
                component: PackPriceZonesComponent,
                data: {
                    breadcrumb: 'PACK.PRICE_ZONES.TITLE'
                },
                canDeactivate: [unsavedChangesGuard()]
            }
        ]
    }
];
