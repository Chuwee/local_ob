import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    GetProductsDeliveryPointsRequest,
    ProductsDeliveryPointsService
} from '@admin-clients/cpanel/products/delivery-points/data-access';
import { ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { ProductEventsService } from '@admin-clients/cpanel-products-my-products-events-data-access';
import { EmptyStateComponent } from '@admin-clients/shared/common/ui/components';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnDestroy, OnInit, inject, viewChild } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatDrawer, MatDrawerContainer, MatDrawerContent } from '@angular/material/sidenav';
import { RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { tap } from 'rxjs';
import { ProductEventsListComponent } from '../list/product-events-list.component';

@Component({
    selector: 'app-product-events-container',
    imports: [
        TranslatePipe, RouterModule, ProductEventsListComponent, EmptyStateComponent, MatDrawerContainer, MatDrawer,
        MatDrawerContent, MatIcon, AsyncPipe, MatButton
    ],
    templateUrl: './product-events-container.component.html',
    styleUrls: ['./product-events-container.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductEventsContainerComponent implements OnInit, OnDestroy {
    readonly #productsSrv = inject(ProductsService);
    readonly #productEventsSrv = inject(ProductEventsService);
    readonly #deliveryPointsSrv = inject(ProductsDeliveryPointsService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #onDestroy = inject(DestroyRef);

    listComponent = viewChild<ProductEventsListComponent>(ProductEventsListComponent);

    readonly #deliveryPointsRequestBody = new GetProductsDeliveryPointsRequest();

    readonly productEvents$ = this.#productEventsSrv.productEvents.list.get$();
    readonly isLoading$ = this.#productEventsSrv.productEvents.list.loading$();

    readonly canWrite$ = this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.EVN_MGR]);

    ngOnInit(): void {
        this.#productsSrv.product.get$().pipe(
            takeUntilDestroyed(this.#onDestroy),
            tap(product => {
                this.#deliveryPointsRequestBody.limit = 1000;
                this.#deliveryPointsRequestBody.entityId = product.entity.id;
                this.#productEventsSrv.productEvents.list.load(product.product_id);
                this.#deliveryPointsSrv.productsDeliveryPointsList.load(this.#deliveryPointsRequestBody);
            })
        ).subscribe();
    }

    ngOnDestroy(): void {
        this.#productEventsSrv.productEvents.list.clear();
        this.#deliveryPointsSrv.productsDeliveryPointsList.clear();
    }

    newEvents(): void {
        this.listComponent().openNewEventsDialog();
    }

    filtersApplied(): boolean {
        return this.listComponent()?.listFilterComponent()?.activeFilters > 0 || false;
    }

}
