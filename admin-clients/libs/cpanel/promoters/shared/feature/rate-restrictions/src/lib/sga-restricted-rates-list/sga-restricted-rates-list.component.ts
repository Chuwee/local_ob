import { EventSessionsService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { Rate, RATE_RESTRICTIONS_SERVICE, RateRestrictions } from '@admin-clients/cpanel/promoters/shared/data-access';
import { EmptyStateComponent } from '@admin-clients/shared/common/ui/components';
import { PrefixPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, input, OnDestroy, OnInit } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatDivider, MatListModule } from '@angular/material/list';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, filter, map } from 'rxjs';

@Component({
    selector: 'app-sga-restricted-rates-list',
    imports: [
        TranslatePipe, MatExpansionModule, MatListModule, AsyncPipe, MatPaginator, MatProgressSpinnerModule,
        MatDivider, PrefixPipe, EmptyStateComponent
    ],
    templateUrl: './sga-restricted-rates-list.component.html',
    styleUrls: ['./sga-restricted-rates-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})

export class SgaRestrictedRatesListComponent implements OnDestroy, OnInit {
    ratePages = new Map<number, BehaviorSubject<number>>();

    readonly #rateRestrictionsSrv = inject(RATE_RESTRICTIONS_SERVICE);
    readonly #sessionsSrv = inject(EventSessionsService);

    readonly $contextId = input.required<number>({ alias: 'contextId' });
    readonly $sessionId = input.required<number>({ alias: 'sessionId' });
    readonly $rates = input.required<Rate[]>({ alias: 'rates' });

    readonly defaultPageSize = 10;

    readonly $isLoading = toSignal(booleanOrMerge([
        this.#rateRestrictionsSrv.ratesRestrictions.inProgress$()
    ]));

    readonly filteredPriceTypesByRate$ = combineLatest([
        toObservable(this.$rates),
        this.#rateRestrictionsSrv.ratesRestrictions.get$(),
        this.#sessionsSrv.getSessionPriceTypes$()
    ]).pipe(
        filter(data => data.every(Boolean)),
        map(([rates, ratesRestrictions, priceTypes]) => rates.map(rate => {
            const rateRestriction =
                ratesRestrictions?.find((restriction: RateRestrictions) => restriction.rate.id === rate.id);
            const restrictedIds = new Set(rateRestriction?.restrictions?.price_type_restriction?.restricted_price_type_ids || []);

            const filteredPriceTypes = priceTypes.filter(piceType => !restrictedIds.has(piceType.id));
            const maxItem = rateRestriction?.restrictions?.max_item_restriction;

            return {
                ...rate,
                filteredPriceTypes,
                maxItem
            };
        }))
    );

    openedId: number = null;

    ngOnInit(): void {
        this.#rateRestrictionsSrv.ratesRestrictions.load(this.$contextId());
        this.#sessionsSrv.loadSessionPriceTypes(this.$contextId(), this.$sessionId());
    }

    ngOnDestroy(): void {
        this.#rateRestrictionsSrv.ratesRestrictions.clear();
        this.#sessionsSrv.clearPriceTypesRestriction();
    }

    getPagedPriceTypes(rate: { id: number; filteredPriceTypes: any[] }): any[] {
        if (!this.ratePages.has(rate.id)) {
            this.ratePages.set(rate.id, new BehaviorSubject(0));
        }
        const page = this.ratePages.get(rate.id).value;
        const start = page * this.defaultPageSize;
        const end = start + this.defaultPageSize;
        return rate.filteredPriceTypes.slice(start, end);
    }

    pageFilter(event: PageEvent, rateId: number): void {
        this.ratePages.get(rateId)?.next(event.pageIndex);
    }

    getPaginatorStartItem(page: number): number {
        return (page * this.defaultPageSize) + 1;
    }

    getPaginatorEndItem(page: number, total: number): number {
        return Math.min((page + 1) * this.defaultPageSize, total);
    }

}
