import { ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { GoBackComponent, NavTabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { AsyncPipe, NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnDestroy, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map } from 'rxjs';

@Component({
    selector: 'app-product-details',
    imports: [
        RouterModule, GoBackComponent, NavTabsMenuComponent, TranslatePipe,
        NgClass, AsyncPipe
    ],
    templateUrl: './product-details.component.html',
    styleUrls: ['./product-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductDetailsComponent implements OnDestroy, OnInit {
    readonly #productsSrv = inject(ProductsService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    #destroyRef = inject(DestroyRef);

    readonly product$ = this.#productsSrv.product.get$();
    readonly entity$ = this.#entitiesSrv.getEntity$();
    readonly isDeliveryConfigSetted$ = this.#productsSrv.product.delivery.get$().pipe(map(Boolean));

    ngOnInit(): void {
        this.#productsSrv.product.get$().pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(product => {
            this.#productsSrv.product.delivery.load(product.product_id);
        });
    }

    ngOnDestroy(): void {
        this.#productsSrv.product.clear();
        this.#entitiesSrv.clearEntity();
        this.#productsSrv.product.delivery.clear();
    }
}
