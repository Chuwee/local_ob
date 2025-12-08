import { ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { ProductsSaleRequestsService } from '@admin-clients/cpanel-channels-products-sale-requests-data-access';
import { PromotionStatus } from '@admin-clients/cpanel-common-promotions-utility-models';
import { EmptyStateComponent } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { LocalCurrencyPipe, LocalNumberPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatDivider } from '@angular/material/divider';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, shareReplay } from 'rxjs';

@Component({
    selector: 'app-product-sale-request-promotions',
    imports: [
        TranslatePipe, AsyncPipe, LocalNumberPipe, LocalCurrencyPipe, UpperCasePipe,
        MatProgressSpinner, MatDivider,
        FormContainerComponent, EmptyStateComponent
    ],
    templateUrl: './product-sale-request-promotions.component.html',
    styleUrls: ['./product-sale-request-promotions.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductSaleRequestPromotionsComponent implements OnInit, OnDestroy {
    readonly #productsSaleRequestsSrv = inject(ProductsSaleRequestsService);
    readonly #productsSrv = inject(ProductsService);

    readonly saleRequest$ = this.#productsSaleRequestsSrv.productSaleRequest.get$()
        .pipe(filter(Boolean), shareReplay({ bufferSize: 1, refCount: true }));

    readonly currency$ = this.saleRequest$.pipe(map(saleRequest => saleRequest.product.currency_code));
    readonly $productId = toSignal(this.saleRequest$.pipe(map(saleRequest => saleRequest.product.product_id)));
    readonly promotionsList$ = this.#productsSrv.product.promotionList.getData$().pipe(filter(Boolean));
    readonly promotionsMetadata$ = this.#productsSrv.product.promotionList.getMetadata$().pipe(filter(Boolean));

    readonly isLoading$ = booleanOrMerge([
        this.#productsSaleRequestsSrv.productSaleRequest.inProgress$(),
        this.#productsSrv.product.promotionList.loading$()
    ]);

    ngOnInit(): void {
        this.#productsSrv.product.promotionList.load(this.$productId(), { limit: 999, offset: 0, status: PromotionStatus.active });
    }

    ngOnDestroy(): void {
        this.#productsSrv.product.promotionList.clear();
    }
}
