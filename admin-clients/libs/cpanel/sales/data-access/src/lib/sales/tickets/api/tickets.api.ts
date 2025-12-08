import { buildHttpParams } from '@OneboxTM/utils-http';
import { ListResponse } from '@OneboxTM/utils-state';
import { GetProductsRequest, Product } from '@admin-clients/cpanel/products/my-products/data-access';
import { TicketsBaseApi } from '@admin-clients/shared/common/data-access';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GetFilterRequest } from '../../models/get-filter-request.model';
import { GetFilterResponse } from '../../models/get-filter-response.model';
import { GetFilterSessionDataRequest } from '../models/get-filter-session-data-request.model';
import { GetFilterSessionDataResponse } from '../models/get-filter-session-data-response.model';
import { TicketRelocation } from '../models/ticket-relocation.model';

@Injectable()
export class TicketsApi extends TicketsBaseApi {

    getFilterOptions$(filterName: string, request: GetFilterRequest): Observable<GetFilterResponse> {
        const params = buildHttpParams(request);
        return this.http.get<GetFilterResponse>(this.TICKETS_BASE_URL + '/filters/' + filterName, { params });
    }

    getFilterSectors(request: GetFilterSessionDataRequest): Observable<GetFilterSessionDataResponse> {
        const params = buildHttpParams(request);
        return this.http.get<GetFilterSessionDataResponse>(this.TICKETS_BASE_URL + '/filters/sectors', { params });
    }

    getFilterPriceTypes(request: GetFilterSessionDataRequest): Observable<GetFilterSessionDataResponse> {
        const params = buildHttpParams(request);
        return this.http.get<GetFilterSessionDataResponse>(this.TICKETS_BASE_URL + '/filters/price-types', { params });
    }

    // endpoint not ready yet
    getFilterProducts(request: GetProductsRequest): Observable<ListResponse<Product>> {
        const params = buildHttpParams(request);
        return this.http.get<ListResponse<Product>>(this.TICKETS_BASE_URL + '/filters/products', { params });
    }

    getTicketRelocations(orderCode: string, ticketId: number): Observable<TicketRelocation[]> {
        return this.http.get<TicketRelocation[]>(this.ORDERS_BASE_URL + `/${orderCode}/items/${ticketId}/relocation-history`);
    }

}
