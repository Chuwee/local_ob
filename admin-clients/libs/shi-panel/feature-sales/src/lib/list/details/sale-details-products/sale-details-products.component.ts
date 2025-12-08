import {
    SearchablePaginatedListComponent, SearchablePaginatedSelectionLoadEvent
} from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, OnDestroy, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, Subject } from 'rxjs';
import { filter, map, shareReplay } from 'rxjs/operators';
import { SalesService } from '../../../sales.service';

const PAGE_SIZE = 4;

@Component({
    selector: 'app-sale-details-products',
    templateUrl: './sale-details-products.component.html',
    styleUrls: ['./sale-details-products.component.scss'],
    imports: [TranslatePipe, SearchablePaginatedListComponent, FlexLayoutModule],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SaleProductsComponent implements OnDestroy {
    private readonly _onDestroy = new Subject<void>();
    private readonly _salesSrv = inject(SalesService);

    private readonly _filter = new BehaviorSubject({
        offset: 0
    });

    readonly pageSize = PAGE_SIZE;
    readonly saleProducts$ = combineLatest([
        this._salesSrv.details.getData$().pipe(filter(Boolean)),
        this._filter.asObservable()
    ])
        .pipe(
            map(([saleDetails, filter]) => ({
                data: saleDetails.products.slice(filter.offset, filter.offset + this.pageSize),
                metadata: { total: saleDetails.products.length, offset: filter.offset, limit: this.pageSize }
            })),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    readonly totalProducts$ = this.saleProducts$.pipe(map(ce => ce.metadata?.total));
    readonly saleProductsList$ = this.saleProducts$.pipe(map(ce => ce.data));
    readonly saleProductsMetadata$ = this.saleProducts$.pipe(map(ce => ce.metadata));

    readonly reqInProgress$ = this._salesSrv.details.loading$();

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    loadPagedSaleProducts({ offset }: SearchablePaginatedSelectionLoadEvent): void {
        this._filter.next({
            ...this._filter.value,
            offset
        });
    }
}
