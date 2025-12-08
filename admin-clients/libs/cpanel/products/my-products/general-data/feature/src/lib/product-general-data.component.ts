import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { Product, ProductStatus, ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { StatusSelectComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatDividerModule } from '@angular/material/divider';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, filter, finalize, map, shareReplay, tap } from 'rxjs';

@Component({
    selector: 'app-product-general-data',
    imports: [
        AsyncPipe, UpperCasePipe,
        MatButtonToggleModule, MatDividerModule,
        RouterModule, TranslatePipe, MaterialModule,
        LastPathGuardListenerDirective, StatusSelectComponent
    ],
    templateUrl: './product-general-data.component.html',
    styleUrls: ['./product-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductGeneralDataComponent implements OnInit, OnDestroy {
    readonly #route = inject(ActivatedRoute);
    readonly #router = inject(Router);
    readonly #productsSrv = inject(ProductsService);

    readonly productHasVariants$ = this.#productsSrv.product.variants.getMetadata$().pipe(
        filter(Boolean),
        map(variantsMeta => variantsMeta.total > 0)
    );

    readonly productStatusList = [ProductStatus.active, ProductStatus.inactive];
    readonly deepPath$ = getDeepPath$(this.#router, this.#route);
    product$: Observable<Product>;

    ngOnInit(): void {
        this.product$ = this.#productsSrv.product.get$().pipe(
            filter(Boolean),
            tap(product => this.#productsSrv.product.variants.loadIfNull(product.product_id)),
            map(product => ({
                ...product,
                status: product.product_state,
                id: product.product_id
            })),
            shareReplay({ bufferSize: 1, refCount: true })
        );
    }

    ngOnDestroy(): void {
        this.#productsSrv.product.variants.clear();
        this.#productsSrv.product.variantsTable.clear();
    }

    updateStatus: (id: number, status: ProductStatus) => Observable<void> = (id, status) =>
        this.#productsSrv.product.update(id, { product_state: status }).pipe(
            finalize(() => {
                this.#productsSrv.product.load(id);
            })
        );
}
