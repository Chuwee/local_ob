import { buildHttpParams, getRangeParam } from '@OneboxTM/utils-http';
import { ListResponse } from '@OneboxTM/utils-state';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { ExportRequest, ExportResponse, Id, IdNameListResponse, PageableFilter } from '@admin-clients/shared/data-access/models';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { CustomerContentImage, PutCustomerContentImage } from '../models/customer-content-image.model';
import { CustomerFilter } from '../models/customer-filter.model';
import { CustomerFriends, PostCustomerFriend } from '../models/customer-friends.model';
import { CustomerLoyaltyPointsResponse, PostLoyaltyPoints } from '../models/customer-loyalty-points.model';
import { CustomersImportResponse, PostCustomersToImport } from '../models/customer-to-import.model';
import { CustomerTypesHistoricItem } from '../models/customer-types-historic-item.model';
import {
    Customer, CustomerFormField, GetCustomersRequest,
    GetCustomersResponse, PostCustomer, PutCustomer
} from '../models/customer.model';
import { PostCustomerPasswordRequest } from '../models/set-customer-password.model';

@Injectable({
    providedIn: 'root'
})
export class CustomersApi {

    readonly #BASE_API = inject(APP_BASE_API);
    readonly #CUSTOMERS_API = `${this.#BASE_API}/customers-mgmt-api/v1/customers`;
    readonly #FORMS_API = `${this.#BASE_API}/customers-mgmt-api/v1/forms`;

    readonly #http = inject(HttpClient);

    getCustomers(request: GetCustomersRequest): Observable<GetCustomersResponse> {
        const params = this.buildGetCustomersParams(request);

        return this.#http.get<GetCustomersResponse>(this.#CUSTOMERS_API, {
            params
        });
    }

    getCustomer(customerId: string, entityId?: string): Observable<Customer> {
        const params = buildHttpParams({ entity_id: entityId });
        return this.#http.get<Customer>(`${this.#CUSTOMERS_API}/${customerId}`, { params });
    }

    postCustomer(customer: PostCustomer): Observable<Id> {
        return this.#http.post<Id>(this.#CUSTOMERS_API, customer);
    }

    putCustomer(customerId: string, putCustomer: PutCustomer, entityId?: string): Observable<void> {
        const params = buildHttpParams({ entity_id: entityId });
        return this.#http.put<void>(`${this.#CUSTOMERS_API}/${customerId}`, putCustomer, { params });
    }

    deleteCustomer(customerId: string, entityId?: string): Observable<void> {
        const params = buildHttpParams({ entity_id: entityId });
        return this.#http.delete<void>(`${this.#CUSTOMERS_API}/${customerId}`, { params });
    }

    getCustomerImage(customerId: string, entityId: number): Observable<CustomerContentImage> {
        const params = buildHttpParams({ entity_id: entityId, type: 'AVATAR' });
        return this.#http.get<CustomerContentImage>(`${this.#CUSTOMERS_API}/${customerId}/images`, { params });
    }

    putCustomerImage(customerId: string, putCustomerImage: PutCustomerContentImage, entityId: number): Observable<void> {
        const params = buildHttpParams({ entity_id: entityId });
        return this.#http.put<void>(`${this.#CUSTOMERS_API}/${customerId}/images`, putCustomerImage, { params });
    }

    deleteCustomerImage(customerId: string, entityId: number): Observable<void> {
        const params = buildHttpParams({ entity_id: entityId, type: 'AVATAR' });
        return this.#http.delete<void>(`${this.#CUSTOMERS_API}/${customerId}/images`, { params });
    }

    lockCustomer(customerId: string, entityId?: string): Observable<void> {
        const params = buildHttpParams({ entity_id: entityId });
        return this.#http.post<void>(`${this.#CUSTOMERS_API}/${customerId}/lock`, {}, { params });
    }

    unLockCustomer(customerId: string, entityId?: string): Observable<void> {
        const params = buildHttpParams({ entity_id: entityId });
        return this.#http.post<void>(`${this.#CUSTOMERS_API}/${customerId}/unlock`, {}, { params });
    }

    lockMultipleCustomers(customerIds: string[]): Observable<void> {
        return this.#http.post<void>(`${this.#CUSTOMERS_API}/lock`, { customer_ids: customerIds });
    }

    unLockMultipleCustomers(customerIds: string[]): Observable<void> {
        return this.#http.post<void>(`${this.#CUSTOMERS_API}/unlock`, { customer_ids: customerIds });
    }

    postCustomerImport(postCustomerToImport: PostCustomersToImport): Observable<CustomersImportResponse> {
        return this.#http.post<CustomersImportResponse>(`${this.#CUSTOMERS_API}/import`, postCustomerToImport);
    }

    getCustomerImport(): Observable<CustomersImportResponse> {
        return this.#http.get<CustomersImportResponse>(`${this.#CUSTOMERS_API}/pending-import`);
    }

    exportCustomers(request: GetCustomersRequest, body: ExportRequest): Observable<ExportResponse> {
        const params = this.buildGetCustomersParams(request);

        return this.#http.post<ExportResponse>(this.#CUSTOMERS_API + '/export', body, {
            params
        });
    }

    getFilterOptions(filter: CustomerFilter, request: PageableFilter): Observable<IdNameListResponse> {
        const params = buildHttpParams({ limit: request.limit, offset: request.offset });
        return this.#http.get<IdNameListResponse>(`${this.#CUSTOMERS_API}/filters/${filter}`, { params });
    }

    postCustomerPassword(customerId: string, entityId: number, request: PostCustomerPasswordRequest): Observable<void> {
        const params = buildHttpParams({ entity_id: entityId });
        return this.#http.post<void>(`${this.#CUSTOMERS_API}/${customerId}/activations`, request, { params });
    }

    resetChangeSeatCounter(entityId: number): Observable<void> {
        const params = buildHttpParams({ entity_id: entityId });
        return this.#http.post<void>(`${this.#CUSTOMERS_API}/seat-reallocation/reset`, null, { params });
    }

    getCustomerLoyaltyPoints(customerId: string, entityId: string, request: PageableFilter): Observable<CustomerLoyaltyPointsResponse> {
        const params = buildHttpParams({ entity_id: entityId, limit: request.limit, offset: request.offset, q: request.q });
        return this.#http.get<CustomerLoyaltyPointsResponse>(`${this.#CUSTOMERS_API}/${customerId}/loyalty-points`,
            { params });
    }

    postCustomerLoyaltyPoints(customerId: string, postLoyaltyPoints: PostLoyaltyPoints, entityId: number): Observable<void> {
        const params = buildHttpParams({ entity_id: entityId });
        return this.#http.post<void>(`${this.#CUSTOMERS_API}/${customerId}/loyalty-points`, postLoyaltyPoints, { params });
    }

    getCustomerForm(form: string, entityId: number): Observable<CustomerFormField[][]> {
        const params = buildHttpParams({ entity_id: entityId });
        return this.#http.get<CustomerFormField[][]>(`${this.#FORMS_API}/${form}`, { params });
    }

    getCustomerFriends(customerId: string, request?: PageableFilter): Observable<ListResponse<CustomerFriends>> {
        const params = request ? buildHttpParams(request) : {};
        return this.#http.get<ListResponse<CustomerFriends>>(`${this.#CUSTOMERS_API}/${customerId}/friends`, { params });
    }

    postCustomerFriend(customerId: string, newFriend: PostCustomerFriend): Observable<void> {
        return this.#http.post<void>(`${this.#CUSTOMERS_API}/${customerId}/friends`, newFriend);
    }

    deleteCustomerFriend(customerId: string, friendId: string): Observable<void> {
        return this.#http.delete<void>(`${this.#CUSTOMERS_API}/${customerId}/friends/${friendId}`);
    }

    deleteMultipleCustomerFriends(customerId: string, friendIds: string[]): Observable<void> {
        const params = buildHttpParams({ ids: friendIds });
        return this.#http.delete<void>(`${this.#CUSTOMERS_API}/${customerId}/friends`, { params });
    }

    getCustomersFriendOf(customerId: string, request?: PageableFilter): Observable<ListResponse<CustomerFriends>> {
        const params = request ? buildHttpParams(request) : {};
        return this.#http.get<ListResponse<CustomerFriends>>(`${this.#CUSTOMERS_API}/${customerId}/friend-of`, { params });
    }

    postImpersonate(customerId: string): Observable<{ token: string }> {
        return this.#http.post<{ token: string }>(`${this.#CUSTOMERS_API}/${customerId}/impersonate`, {});
    }

    getCustomerTypesHistoric(customerId: string, entityId?: string): Observable<CustomerTypesHistoricItem[]> {
        const params = buildHttpParams({ entity_id: entityId });
        return this.#http.get<CustomerTypesHistoricItem[]>(`${this.#CUSTOMERS_API}/${customerId}/customer-types/historic`, { params });
    }

    private buildGetCustomersParams(request: GetCustomersRequest): HttpParams {
        let sort = request.sort;
        if (sort) {
            const sortParts = sort.split(':');
            if (sortParts?.length > 1 && sortParts[0] === 'name') {
                sort += `,surname:${sortParts[1]}`;
            }
        }
        return buildHttpParams({
            q: request.q,
            limit: request.limit,
            offset: request.offset,
            entity_id: request.entityId,
            product_id: request.productId,
            client_id: request.clientId,
            type: request.type,
            sort,
            status: request.status,
            sign_up_date: getRangeParam(request.startDate, request.endDate)
        });
    }
}