import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import {
    ProductsSaleRequestsService
} from '@admin-clients/cpanel-channels-products-sale-requests-data-access';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatDividerModule } from '@angular/material/divider';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, tap } from 'rxjs';

@Component({
    selector: 'app-product-sale-request-communication',
    standalone: true,
    imports: [
        AsyncPipe, RouterModule, TranslatePipe,
        MatButtonToggleModule, MatDividerModule,
        LastPathGuardListenerDirective
    ],
    templateUrl: './product-sale-request-communication.component.html',
    styleUrls: ['./product-sale-request-communication.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductSaleRequestCommunicationComponent {
    readonly #route = inject(ActivatedRoute);
    readonly #router = inject(Router);
    readonly #productsSaleRequestsSrv = inject(ProductsSaleRequestsService);
    readonly #productSrv = inject(ProductsService);

    readonly saleRequest$ = this.#productsSaleRequestsSrv.productSaleRequest.get$().pipe(
        filter(Boolean),
        tap(request => this.#productSrv.product.load(request.product.product_id))
    );

    readonly deepPath$ = getDeepPath$(this.#router, this.#route);

}
