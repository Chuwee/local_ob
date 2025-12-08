import { getListData, getMetadata, mapMetadata, StateManager } from '@OneboxTM/utils-state';
import { inject, Injectable } from '@angular/core';
import { ProductsSaleRequestsApi } from './api/products-sale-requests.api';
import { GetProductsSaleRequestsReq, ProductSaleRequestStatus } from './models/product-sale-request.model';
import { ProductsSaleRequestsState } from './state/products-sale-requests.state';

@Injectable()
export class ProductsSaleRequestsService {
    readonly #api = inject(ProductsSaleRequestsApi);
    readonly #state = inject(ProductsSaleRequestsState);

    readonly productsSaleRequestsList = Object.freeze({
        load: (request: GetProductsSaleRequestsReq) => StateManager.load(
            this.#state.productsSaleRequestsList,
            this.#api.getProductSaleRequests(request).pipe(mapMetadata())
        ),
        getData$: () => this.#state.productsSaleRequestsList.getValue$().pipe(getListData()),
        getMetadata$: () => this.#state.productsSaleRequestsList.getValue$().pipe(getMetadata()),
        loading$: () => this.#state.productsSaleRequestsList.isInProgress$(),
        clear: () => this.#state.productsSaleRequestsList.setValue(null)
    });

    readonly productSaleRequest = Object.freeze({
        load: (saleRequestId: number) => StateManager.load(
            this.#state.productSaleRequest,
            this.#api.getProductSaleRequest(saleRequestId)
        ),
        get$: () => this.#state.productSaleRequest.getValue$(),
        error$: () => this.#state.productSaleRequest.getError$(),
        inProgress$: () => this.#state.productSaleRequest.isInProgress$(),
        clear: () => this.#state.productSaleRequest.setValue(null),
        status: Object.freeze({
            update: (productSaleRequestId: number, status: ProductSaleRequestStatus) => StateManager.inProgress(
                this.#state.productsSaleRequestStatus,
                this.#api.putProductSaleRequestStatus(productSaleRequestId, status)
            ),
            inProgress$: () => this.#state.productsSaleRequestStatus.isInProgress$()
        })
    });
}
