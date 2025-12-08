import { ProductsSaleRequestsService } from '@admin-clients/cpanel-channels-products-sale-requests-data-access';
import { GoBackComponent, NavTabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';

@Component({
    selector: 'app-product-sale-request-details',
    imports: [
        RouterOutlet, GoBackComponent, NavTabsMenuComponent, AsyncPipe
    ],
    templateUrl: './product-sale-request-details.component.html',
    styleUrls: ['./product-sale-request-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductSaleRequestDetailsComponent implements OnDestroy {
    readonly #productsSaleRequestsSrv = inject(ProductsSaleRequestsService);

    readonly saleRequest$ = this.#productsSaleRequestsSrv.productSaleRequest.get$().pipe(filter(Boolean));

    ngOnDestroy(): void {
        this.#productsSaleRequestsSrv.productSaleRequest.clear();
    }
}
