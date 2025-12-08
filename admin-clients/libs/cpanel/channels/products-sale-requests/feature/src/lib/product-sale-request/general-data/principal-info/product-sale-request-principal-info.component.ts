import {
    ProductChannelContentImageType, ProductsService, ProductType, ProductVariantStatus
} from '@admin-clients/cpanel/products/my-products/data-access';
import { ProductsSaleRequestsService } from '@admin-clients/cpanel-channels-products-sale-requests-data-access';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { MatList, MatListItem } from '@angular/material/list';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, filter, map, shareReplay } from 'rxjs';

@Component({
    selector: 'app-product-sale-request-principal-info',
    imports: [
        TranslatePipe, AsyncPipe, UpperCasePipe,
        MatProgressSpinner, MatIcon, MatAccordion, MatExpansionPanel, MatExpansionPanelTitle, MatExpansionPanelHeader,
        MatPaginator, MatList, MatListItem,
        FormContainerComponent
    ],
    templateUrl: './product-sale-request-principal-info.component.html',
    styleUrls: ['./product-sale-request-principal-info.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductSaleRequestPrincipalInfoComponent implements OnInit, OnDestroy {
    readonly #productsSaleRequestsSrv = inject(ProductsSaleRequestsService);
    readonly #productsSrv = inject(ProductsService);

    readonly saleRequest$ = this.#productsSaleRequestsSrv.productSaleRequest.get$().pipe(
        filter(Boolean),
        shareReplay({ bufferSize: 1, refCount: true })
    );

    readonly imageUrl$ = combineLatest([
        this.#productsSrv.product.channelContents.images.get$(),
        this.saleRequest$
    ]).pipe(
        filter(resp => resp.every(Boolean)),
        map(([images, saleRequest]) => {
            const language = saleRequest.languages.default || saleRequest.languages.selected?.[0];
            if (images?.length && language) {
                const imageToShow = images.find(image =>
                    image.language === language && image.type === ProductChannelContentImageType.landscape);
                return imageToShow.image_url;
            }
            return null;
        })
    );

    readonly hasVariants$ = this.#productsSrv.product.variants.getMetadata$().pipe(filter(Boolean), map(metadata => metadata.total > 0));
    readonly variants$ = this.#productsSrv.product.variants.getData$().pipe(filter(Boolean));
    readonly variantsMetadata$ = this.#productsSrv.product.variants.getMetadata$().pipe(filter(Boolean));

    readonly isLoading$ = booleanOrMerge([
        this.#productsSaleRequestsSrv.productSaleRequest.inProgress$(),
        this.#productsSrv.product.channelContents.images.loading$(),
        this.#productsSrv.product.variants.loading$()
    ]);

    readonly $productId = toSignal(this.saleRequest$.pipe(map(saleRequest => saleRequest.product.product_id)));
    readonly $hasContactData = toSignal(this.saleRequest$.pipe(
        map(saleRequest => saleRequest.product?.contact_person && Object.keys(saleRequest.product?.contact_person)?.length)
    ));

    readonly $isProductWithVariants = toSignal(
        this.saleRequest$.pipe(map(saleRequest => saleRequest.product.product_type === ProductType.variant))
    );

    readonly variantsPageSize = 5;

    ngOnInit(): void {
        this.#productsSrv.product.channelContents.images.load(this.$productId());
        if (this.$isProductWithVariants()) {
            this.loadVariants({ pageIndex: 0 });
        }
    }

    ngOnDestroy(): void {
        this.#productsSrv.product.channelContents.images.clear();
        this.#productsSrv.product.variants.clear();
    }

    loadVariants(pageOptions: Partial<PageEvent>): void {
        const request = {
            limit: this.variantsPageSize,
            offset: this.variantsPageSize * pageOptions.pageIndex,
            status: ProductVariantStatus.active
        };
        this.#productsSrv.product.variants.load(this.$productId(), request);
    }
}
