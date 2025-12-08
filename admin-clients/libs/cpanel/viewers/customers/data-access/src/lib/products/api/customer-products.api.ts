import { buildHttpParams } from '@OneboxTM/utils-http';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable } from 'rxjs';
import { GetCustomerExternalProductsResponse } from '../models/customer-external-products.model';
import { CustomerProductsFilters, GetCustomerProductsResponse } from '../models/customer-product.model';

export class CustomerProductsApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly CUSTOMERS_API = `${this.BASE_API}/customers-mgmt-api/v1/customers`;

    private readonly _http = inject(HttpClient);

    getCustomerProducts$(customerId: string, entityId: string, request: CustomerProductsFilters): Observable<GetCustomerProductsResponse> {
        const params = buildHttpParams({
            limit: request.limit,
            offset: request.offset,
            sort: request.sort,
            entity_id: entityId,
            product_type: request.product_type
        });
        return this._http.get<GetCustomerProductsResponse>(`${this.CUSTOMERS_API}/${customerId}/products`, { params });
    }

    getCustomerExternalProducts$(
        customerId: string,
        entityId: string
    ): Observable<GetCustomerExternalProductsResponse> {
        const params = buildHttpParams({ entity_id: entityId });
        return this._http.get<GetCustomerExternalProductsResponse>(
            `${this.CUSTOMERS_API}/${customerId}/external-products`
            , { params }
        );
    }
}
