import {
    SaleRequestPromotionValidityPeriodType, SalesRequestsService
} from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { PromotionDiscountType } from '@admin-clients/cpanel-common-promotions-utility-models';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { first, map, shareReplay } from 'rxjs/operators';

@Component({
    selector: 'app-sale-request-promotions',
    templateUrl: './sale-request-promotions.component.html',
    styleUrls: ['./sale-request-promotions.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SaleRequestPromotionsComponent implements OnInit, OnDestroy {
    private readonly _salesRequestsService = inject(SalesRequestsService);

    readonly dateTimeFormats = DateTimeFormats;
    readonly promotionDiscountTypes = PromotionDiscountType;
    readonly saleRequestPromotionValidityPeriodTypes = SaleRequestPromotionValidityPeriodType;
    readonly isLoading$ = booleanOrMerge([
        this._salesRequestsService.isSaleRequestLoading$(),
        this._salesRequestsService.isSaleRequestPromotionsLoading$()
    ]);

    readonly saleRequestPromotions$ = this._salesRequestsService.getSaleRequestPromotions$();
    readonly saleRequestPromotionMetadata$ = this._salesRequestsService.getSaleRequestPromotionsMetadata$();
    readonly currency$ = this._salesRequestsService.getSaleRequest$()
        .pipe(map(saleRequest => saleRequest.event.currency_code), shareReplay({ bufferSize: 1, refCount: true }));

    ngOnInit(): void {
        this._salesRequestsService.getSaleRequest$()
            .pipe(first(Boolean))
            .subscribe(saleRequest => {
                this._salesRequestsService.loadSaleRequestPromotions({ saleRequestId: saleRequest.id, limit: 999, offset: 0 });
            });
    }

    ngOnDestroy(): void {
        this._salesRequestsService.clearSaleRequestPromotions();
    }

}
