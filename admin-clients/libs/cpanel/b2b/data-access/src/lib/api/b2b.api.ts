import { buildHttpParams } from '@OneboxTM/utils-http';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { ExportRequest, ExportResponse } from '@admin-clients/shared/data-access/models';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
    GetB2bClientsRequest, GetB2bClientsResponse, B2bConditionsGroupType, B2bConditionsClient, PostB2bClientUserApiKey,
    GetB2bConditionsRequest, B2bConditions, B2bConditionsClientsGroupType, PutB2bConditionsClients, DeleteB2bConditionsRequest,
    DeleteB2bConditionsClientsRequest, GetB2bConditionsClientsRequest, GetB2bConditionsClientsResponse, PutB2bConditions,
    B2bClient, PutB2bClient, PostB2bClient, B2bClientUser, PutB2bClientUser, PostB2bClientUser, GetB2bConditionsClientRequest
} from '../models';
import { B2bClientOperation, B2bClientOperationType } from '../models/b2b-client-balance-operation.model';
import { B2bClientBalance } from '../models/b2b-client-balance.model';
import { B2bClientTransactionsExportReq, B2bClientTransactionsExportResponse } from '../models/b2b-client-transactions-export.model';
import { GetB2bClientTransactionsRequest, GetB2bClientTransactionsResponse } from '../models/b2b-client-transactions.model';
import { B2bSeat } from '../models/b2b-seat.model';
import { GetB2bClientUsersRequest, GetB2bClientUsersResponse } from '../models/get-b2b-client-users.model';
import { B2bSeatsFilter, GetB2bSeatsFiltersRequest, GetB2bSeatsFiltersResponse } from '../models/get-b2b-seats-filters.model';
import { GetB2bSeatsListRequest, GetB2bSeatsListResponse } from '../models/get-b2b-seats.model';

@Injectable()
export class B2bApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly USERS_SEGMENT = '/users';
    private readonly RESET_PASSWORD_SEGMENT = '/reset-password';
    private readonly API_KEY_REFRESH_SEGMENT = '/apikey/refresh';
    private readonly BALANCE_SEGMENT = '/balance';
    private readonly TRANSACTIONS_SEGMENT = '/transactions';
    private readonly EXPORT_SEGMENT = '/exports';
    private readonly OPERATIONS_SEGMENT = '/operations';
    private readonly SEATS = '/publishing/seats';
    private readonly CLIENTS_SEGMENT = '/clients';
    private readonly CONDITIONS_SEGMENT = '/conditions';
    private readonly B2B_API = `${this.BASE_API}/mgmt-api/v1/b2b`;

    private readonly _http = inject(HttpClient);

    getB2bClients(request: GetB2bClientsRequest): Observable<GetB2bClientsResponse> {
        const params = buildHttpParams(request);
        return this._http.get<GetB2bClientsResponse>(`${this.B2B_API}${this.CLIENTS_SEGMENT}`, { params });
    }

    getB2bConditions<T extends B2bConditionsGroupType>(groupType: T, request: GetB2bConditionsRequest<T>): Observable<B2bConditions> {
        const params = buildHttpParams(request);
        return this._http.get<B2bConditions>(`${this.B2B_API}${this.CONDITIONS_SEGMENT}/${groupType}`, { params });
    }

    putB2bConditionsClients(groupType: B2bConditionsClientsGroupType, body: PutB2bConditionsClients): Observable<void> {
        return this._http.put<void>(
            `${this.B2B_API}${this.CONDITIONS_SEGMENT}/${groupType}${this.CLIENTS_SEGMENT}`, body
        );
    }

    deleteB2bConditions<T extends B2bConditionsGroupType>(groupType: T, req: DeleteB2bConditionsRequest<T>): Observable<void> {
        return this._http.delete<void>(`${this.B2B_API}${this.CONDITIONS_SEGMENT}/${groupType}`, {
            params: req
        });
    }

    deleteB2bConditionsClients<T extends B2bConditionsClientsGroupType>(
        groupType: T, req: DeleteB2bConditionsClientsRequest<T>
    ): Observable<void> {
        return this._http.delete<void>(`${this.B2B_API}${this.CONDITIONS_SEGMENT}/${groupType}${this.CLIENTS_SEGMENT}`, {
            params: req
        });
    }

    getB2bConditionsClients<T extends B2bConditionsClientsGroupType>(
        groupType: T, request: GetB2bConditionsClientsRequest<T>
    ): Observable<GetB2bConditionsClientsResponse> {
        const params = buildHttpParams(request);
        return this._http.get<GetB2bConditionsClientsResponse>(
            `${this.B2B_API}${this.CONDITIONS_SEGMENT}/${groupType}${this.CLIENTS_SEGMENT}`, { params }
        );
    }

    putB2bConditions(groupType: B2bConditionsGroupType, body: PutB2bConditions): Observable<void> {
        return this._http.put<void>(`${this.B2B_API}${this.CONDITIONS_SEGMENT}/${groupType}`, body);
    }

    getB2bClient(clientId: number, entityId?: number): Observable<B2bClient> {
        const params = buildHttpParams({ entity_id: entityId });
        return this._http.get<B2bClient>(`${this.B2B_API}${this.CLIENTS_SEGMENT}/${clientId}`, { params });
    }

    putB2bClient(clientId: number, request: PutB2bClient): Observable<void> {
        return this._http.put<void>(`${this.B2B_API}${this.CLIENTS_SEGMENT}/${clientId}`, request);
    }

    postB2bClient(request: PostB2bClient): Observable<{ id: number }> {
        return this._http.post<{ id: number }>(`${this.B2B_API}${this.CLIENTS_SEGMENT}`, request);
    }

    deleteB2bClient(clientId: number, entityId?: number): Observable<void> {
        const params = buildHttpParams({ entity_id: entityId });
        return this._http.delete<void>(`${this.B2B_API}${this.CLIENTS_SEGMENT}/${clientId}`, { params });
    }

    getB2bClientUsers(clientId: number, request: GetB2bClientUsersRequest): Observable<GetB2bClientUsersResponse> {
        const params = buildHttpParams(request);
        return this._http.get<GetB2bClientUsersResponse>(
            `${this.B2B_API}${this.CLIENTS_SEGMENT}/${clientId}${this.USERS_SEGMENT}`, { params }
        );
    }

    getB2bClientUser(clientId: number, clientUserId: number, entityId?: number): Observable<B2bClientUser> {
        const params = buildHttpParams({ entity_id: entityId });
        return this._http.get<B2bClientUser>(
            `${this.B2B_API}${this.CLIENTS_SEGMENT}/${clientId}${this.USERS_SEGMENT}/${clientUserId}`, { params }
        );
    }

    putB2bClientUser(clientId: number, clientUserId: number, request: PutB2bClientUser): Observable<void> {
        return this._http.put<void>(`${this.B2B_API}${this.CLIENTS_SEGMENT}/${clientId}${this.USERS_SEGMENT}/${clientUserId}`, request);
    }

    postB2bClientUser(clientId: number, request: PostB2bClientUser): Observable<{ id: number }> {
        return this._http.post<{ id: number }>(`${this.B2B_API}${this.CLIENTS_SEGMENT}/${clientId}${this.USERS_SEGMENT}`, request);
    }

    deleteB2bClientUser(clientId: number, clientUserId: number, entityId?: number): Observable<void> {
        const params = buildHttpParams({ entity_id: entityId });
        return this._http.delete<void>(
            `${this.B2B_API}${this.CLIENTS_SEGMENT}/${clientId}${this.USERS_SEGMENT}/${clientUserId}`, { params }
        );
    }

    postB2bClientUserPassword(clientId: number, clientUserId: number, entityId?: number): Observable<void> {
        const params = buildHttpParams({ entity_id: entityId });
        return this._http.post<void>(
            `${this.B2B_API}${this.CLIENTS_SEGMENT}/${clientId}${this.USERS_SEGMENT}/${clientUserId}${this.RESET_PASSWORD_SEGMENT}`,
            null,
            { params }
        );
    }

    postB2bClientUserApiKey(clientId: number, clientUserId: number, entityId: number | null): Observable<PostB2bClientUserApiKey> {
        const params = buildHttpParams({ entity_id: entityId });
        return this._http.post<PostB2bClientUserApiKey>(
            `${this.B2B_API}${this.CLIENTS_SEGMENT}/${clientId}${this.USERS_SEGMENT}/${clientUserId}${this.API_KEY_REFRESH_SEGMENT}`,
            null,
            { params }
        );
    }

    getB2bConditionsClient<T extends B2bConditionsClientsGroupType>(
        groupType: T, clientId: number, request: GetB2bConditionsClientRequest<T>
    ): Observable<B2bConditionsClient> {
        const params = buildHttpParams(request);
        return this._http.get<B2bConditionsClient>(
            `${this.B2B_API}${this.CONDITIONS_SEGMENT}/${groupType}${this.CLIENTS_SEGMENT}/${clientId}`, { params }
        );
    }

    getB2bClientBalance(clientId: number, entityId: number, currencyCode: string): Observable<B2bClientBalance> {
        const params = buildHttpParams({ entity_id: entityId, currency_code: currencyCode });
        return this._http.get<B2bClientBalance>(`${this.B2B_API}${this.CLIENTS_SEGMENT}/${clientId}${this.BALANCE_SEGMENT}`, { params });
    }

    postB2bClientBalance(clientId: number, entityId: number): Observable<void> {
        const params = buildHttpParams({ entity_id: entityId });
        return this._http.post<void>(`${this.B2B_API}${this.CLIENTS_SEGMENT}/${clientId}${this.BALANCE_SEGMENT}`, null, { params });
    }

    getB2bClientTransactions(clientId: number, request: GetB2bClientTransactionsRequest): Observable<GetB2bClientTransactionsResponse> {
        const params = buildHttpParams(request);
        return this._http.get<GetB2bClientTransactionsResponse>(
            `${this.B2B_API}${this.CLIENTS_SEGMENT}/${clientId}${this.BALANCE_SEGMENT}${this.TRANSACTIONS_SEGMENT}`, { params }
        );
    }

    postB2bClientTransactionsExport(
        clientId: number, body: B2bClientTransactionsExportReq
    ): Observable<B2bClientTransactionsExportResponse> {
        return this._http.post<B2bClientTransactionsExportResponse>(
            `${this.B2B_API}${this.CLIENTS_SEGMENT}/${clientId}${this.BALANCE_SEGMENT}${this.TRANSACTIONS_SEGMENT}${this.EXPORT_SEGMENT}`,
            body
        );
    }

    postB2bClientBalanceOperation(clientId: number, operationType: B2bClientOperationType, body: B2bClientOperation): Observable<void> {
        return this._http.post<void>(
            `${this.B2B_API}${this.CLIENTS_SEGMENT}/${clientId}${this.BALANCE_SEGMENT}${this.OPERATIONS_SEGMENT}/${operationType}`, body
        );
    }

    getSeats(request: GetB2bSeatsListRequest): Observable<GetB2bSeatsListResponse> {
        const params = buildHttpParams(request);
        return this._http.get<GetB2bSeatsListResponse>(
            `${this.B2B_API}${this.SEATS}`, { params }
        );
    }

    getSeat(id: number): Observable<B2bSeat> {
        return this._http.get<B2bSeat>(
            `${this.B2B_API}${this.SEATS}/${id.toString()}`
        );
    }

    exportB2bSeatsList(request: GetB2bSeatsListRequest, body: ExportRequest): Observable<ExportResponse> {
        const params = buildHttpParams(request);
        return this._http.post<ExportResponse>(
            `${this.B2B_API}${this.SEATS}/exports`, body, { params }
        );
    }

    getB2bSeatsFilterOptions$(filterName: B2bSeatsFilter, request: GetB2bSeatsFiltersRequest): Observable<GetB2bSeatsFiltersResponse> {
        const params = buildHttpParams(request);
        return this._http.get<GetB2bSeatsFiltersResponse>(`${this.B2B_API}${this.SEATS}/filters/${filterName}`, { params });
    }
}
