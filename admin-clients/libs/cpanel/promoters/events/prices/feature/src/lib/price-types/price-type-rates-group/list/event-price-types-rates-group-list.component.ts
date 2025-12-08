import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { EventsService, RateGroup } from '@admin-clients/cpanel/promoters/events/data-access';
import { ExternalInventoryProviders } from '@admin-clients/shared/common/data-access';
import { EmptyStateComponent } from '@admin-clients/shared/common/ui/components';
import { LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { ActivityTicketType } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, input, OnDestroy, OnInit, signal } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, filter, firstValueFrom } from 'rxjs';
import { first, map, shareReplay } from 'rxjs/operators';

@Component({
    selector: 'app-event-price-types-rates-group-list',
    changeDetection: ChangeDetectionStrategy.OnPush,
    styleUrls: ['./event-price-types-rates-group-list.component.scss'],
    templateUrl: './event-price-types-rates-group-list.component.html',
    imports: [
        MatTableModule, TranslatePipe, EmptyStateComponent, MatTooltip, LocalCurrencyPipe,
        AsyncPipe
    ]
})
export class EventPriceTypesRatesGroupListComponent implements OnInit, OnDestroy {
    readonly #eventService = inject(EventsService);
    readonly #authSrv = inject(AuthenticationService);

    readonly $selectedRate = input<RateGroup>(null, { alias: 'selectedRate' });

    readonly tableHeaders$ = combineLatest([
        this.#eventService.eventPrices.get$(),
        this.#eventService.ratesGroup.get$()
    ]).pipe(
        filter(([_, rates]) => !!rates),
        map(([prices, rates]) => {
            if (prices?.length) {
                const ratesNames = prices.map(price => price.rate.rate_group.name);
                const rateNameSet = new Set(ratesNames);
                return ['VENUE_TPLS.PRICE_TYPE', ...Array.from(rateNameSet)];
            } else {
                if (this.$selectedRate()) {
                    return ['VENUE_TPLS.PRICE_TYPE', this.$selectedRate().name];
                } else {
                    return ['VENUE_TPLS.PRICE_TYPE', ...rates.map(rate => rate.name)];
                }
            }
        }),
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    readonly userCurrency$ = this.#authSrv.getLoggedUser$()
        .pipe(first(), map(user => user.currency));

    readonly pricesRows$ = this.#eventService.eventPrices.get$().pipe(
        map(prices => {
            if (prices?.length) {
                const reducedPricesObj = prices.filter(price => price.ticket_type !== ActivityTicketType.group).reduce((acc, price) => {
                    if (acc[price.price_type.description]) {
                        acc[price.price_type.description][price.rate.rate_group.name] = price.value;
                    } else {
                        acc[price.price_type.description] = {};
                        acc[price.price_type.description]['VENUE_TPLS.PRICE_TYPE'] = price.price_type.description;
                        acc[price.price_type.description][price.rate.rate_group.name] = price.value;
                    }
                    return acc;
                }, {});
                return Object.values(reducedPricesObj);
            } else {
                return [];
            }
        }),
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    readonly $isSga = signal(false);

    async ngOnInit(): Promise<void> {
        this.#eventService.ratesGroup.clear();
        const event = await firstValueFrom(this.#eventService.event.get$());
        this.$isSga.set(event.additional_config.inventory_provider === ExternalInventoryProviders.sga);
        if (this.$isSga()) {
            this.#eventService.sgaProducts.load(event.id);
        } else {
            this.#eventService.ratesGroup.load(event.id);
        }
    }

    ngOnDestroy(): void {
        this.#eventService.ratesGroup.clear();
        this.#eventService.sgaProducts.clear();
    }

}
