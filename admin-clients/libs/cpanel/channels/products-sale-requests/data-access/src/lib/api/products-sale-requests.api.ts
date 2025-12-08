import { buildHttpParams, getRangeParam } from '@OneboxTM/utils-http';
import { ListResponse } from '@OneboxTM/utils-state';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
    GetProductsSaleRequestsReq, ProductSaleRequest, ProductSaleRequestListElem, ProductSaleRequestStatus
} from '../models/product-sale-request.model';

@Injectable()
export class ProductsSaleRequestsApi {
    readonly #BASE_API = inject(APP_BASE_API);
    readonly #PRODUCTS_SALE_REQUESTS_API = `${this.#BASE_API}/mgmt-api/v1/products-sale-requests`;

    readonly #http = inject(HttpClient);

    getProductSaleRequests(request: GetProductsSaleRequestsReq): Observable<ListResponse<ProductSaleRequestListElem>> {
        const params = buildHttpParams({
            q: request.q,
            sort: request.sort,
            limit: request.limit,
            offset: request.offset,
            status: request.status,
            request_date: getRangeParam(request.startDate, request.endDate)
        });
        return this.#http.get<ListResponse<ProductSaleRequestListElem>>(this.#PRODUCTS_SALE_REQUESTS_API, { params });
    }

    putProductSaleRequestStatus(
        productSaleRequestId: number, status: ProductSaleRequestStatus
    ): Observable<{ status: ProductSaleRequestStatus }> {
        return this.#http.put<{ status: ProductSaleRequestStatus }>(
            `${this.#PRODUCTS_SALE_REQUESTS_API}/${productSaleRequestId}`, { status }
        );
    }

    getProductSaleRequest(saleRequestId: number): Observable<ProductSaleRequest> {
        return this.#http.get<ProductSaleRequest>(`${this.#PRODUCTS_SALE_REQUESTS_API}/${saleRequestId}`);
    }
}
