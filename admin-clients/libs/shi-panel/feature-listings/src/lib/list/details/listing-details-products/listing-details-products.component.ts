import {
    SearchablePaginatedListComponent, SearchablePaginatedSelectionLoadEvent
} from '@admin-clients/shared/common/ui/components';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { ChangeDetectionStrategy, Component, OnDestroy, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, Subject } from 'rxjs';
import { filter, map, shareReplay } from 'rxjs/operators';
import { ListingsService } from '../../../listings.service';

const PAGE_SIZE = 4;

@Component({
    selector: 'app-listing-details-products',
    templateUrl: './listing-details-products.component.html',
    styleUrls: ['./listing-details-products.component.scss'],
    imports: [TranslatePipe, MatTooltip, SearchablePaginatedListComponent, FlexLayoutModule, EllipsifyDirective],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ListingProductsComponent implements OnDestroy {
    private readonly _onDestroy = new Subject<void>();
    private readonly _listingsSrv = inject(ListingsService);

    private readonly _filter = new BehaviorSubject({
        offset: 0
    });

    readonly pageSize = PAGE_SIZE;
    readonly listingProducts$ = combineLatest([
        this._listingsSrv.details.getListingData$().pipe(filter(Boolean)),
        this._filter.asObservable()
    ])
        .pipe(
            map(([listingDetails, filter]) => ({
                data: listingDetails.products.slice(filter.offset, filter.offset + this.pageSize),
                metadata: { total: listingDetails.products.length, offset: filter.offset, limit: this.pageSize }
            })),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    readonly totalProducts$ = this.listingProducts$.pipe(map(ce => ce.metadata?.total));
    readonly listingProductsList$ = this.listingProducts$.pipe(map(ce => ce.data));
    readonly listingProductsMetadata$ = this.listingProducts$.pipe(map(ce => ce.metadata));

    readonly reqInProgress$ = this._listingsSrv.details.loading$();

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    loadPagedListingProducts({ offset }: SearchablePaginatedSelectionLoadEvent): void {
        this._filter.next({
            ...this._filter.value,
            offset
        });
    }
}
