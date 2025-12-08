import { eventChannelsProviders, EventChannelsService } from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    RATE_RESTRICTIONS_CHANNELS_SERVICE, RATE_RESTRICTIONS_SERVICE, RateRestrictionsChannelsService
} from '@admin-clients/cpanel/promoters/shared/data-access';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { PrefixPipe } from '@admin-clients/shared/utility/pipes';
import { NgModule } from '@angular/core';
import {
    RouterModule,
    Routes
} from '@angular/router';
import { EventPricesComponent } from './event-prices.component';
import {
    EventPriceTypesComponent
} from './price-types/event-price-types.component';
import { EventRatesComponent } from './rates/event-rates.component';
import { EventSgaProductsListComponent } from './sga-products-list/event-sga-products-list.component';
import {
    EventSurchargesComponent
} from './surcharges/event-surcharges.component';
import { EventTaxesComponent } from './taxes/event-taxes.component';

const routes: Routes = [{
    path: '',
    component: EventPricesComponent,
    data: {
        breadcrumb: 'EVENTS.PRICES_TEMP.TITLE'
    },
    children: [
        {
            path: '',
            // TODO: Change redirecTo when  taxes end
            // redirectTo: 'taxes',
            redirectTo: 'rates',
            pathMatch: 'full'
        },
        {
            path: 'rates',
            component: EventRatesComponent,
            data: {
                breadcrumb: 'EVENTS.RATE'
            },
            providers: [
                PrefixPipe.provider('EVENTS.'),
                ...eventChannelsProviders,
                {
                    provide: RATE_RESTRICTIONS_SERVICE,
                    useExisting: EventsService
                },
                {
                    provide: RATE_RESTRICTIONS_CHANNELS_SERVICE,
                    useFactory: (eventChannelsService: EventChannelsService): RateRestrictionsChannelsService => ({
                        get$: () => eventChannelsService.eventChannelsList.getData$(),
                        load: (id: number) => eventChannelsService.eventChannelsList.load(id, {}),
                        clear: () => eventChannelsService.eventChannelsList.clear(),
                        channelsPath: ['../../channels']
                    }),
                    deps: [EventChannelsService]
                }
            ],
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'sga-products',
            component: EventSgaProductsListComponent,
            data: {
                breadcrumb: 'EVENTS.SGA_PRODUCTS.TITLE'
            },
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'price-types',
            component: EventPriceTypesComponent,
            data: {
                breadcrumb: 'EVENTS.PRICE_TYPES.TITLE'
            },
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'surcharges',
            component: EventSurchargesComponent,
            data: {
                breadcrumb: 'EVENTS.SURCHARGES.TITLE'
            },
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'taxes',
            component: EventTaxesComponent,
            data: {
                breadcrumb: 'EVENTS.TAXES.TITLE'
            },
            canDeactivate: [unsavedChangesGuard()]
        }

    ]
}];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class EventPricesRoutingModule {
}
