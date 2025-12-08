import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import {
    ProductSaleRequestStatus, productSaleRequestStatus, ProductsSaleRequestsService
} from '@admin-clients/cpanel-channels-products-sale-requests-data-access';
import { StatusSelectComponent } from '@admin-clients/shared/common/ui/components';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatDividerModule } from '@angular/material/divider';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, finalize, Observable } from 'rxjs';

@Component({
    selector: 'app-product-sale-request-general-data',
    imports: [
        AsyncPipe, RouterModule, TranslatePipe,
        MatButtonToggleModule, MatDividerModule,
        LastPathGuardListenerDirective, StatusSelectComponent
    ],
    templateUrl: './product-sale-request-general-data.component.html',
    styleUrls: ['./product-sale-request-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductSaleRequestGeneralDataComponent {
    readonly #route = inject(ActivatedRoute);
    readonly #router = inject(Router);
    readonly #productsSaleRequestsSrv = inject(ProductsSaleRequestsService);

    readonly saleRequest$ = this.#productsSaleRequestsSrv.productSaleRequest.get$().pipe(filter(Boolean));
    readonly deepPath$ = getDeepPath$(this.#router, this.#route);
    readonly productSaleRequestsStatus = productSaleRequestStatus;

    updateStatus: (id: number, status: ProductSaleRequestStatus) => Observable<{ status: ProductSaleRequestStatus }> = (id, status) =>
        this.#productsSaleRequestsSrv.productSaleRequest.status.update(id, status).pipe(
            finalize(() => this.#productsSaleRequestsSrv.productSaleRequest.load(id))
        );
}
