import { buildHttpParams } from '@OneboxTM/utils-http';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable } from 'rxjs';
import { GetCustomerNotesResponse, PostCustomerNote, PutCustomerNote } from '../models/customer-note.model';

export class CustomerNotesApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly CUSTOMERS_API = `${this.BASE_API}/customers-mgmt-api/v1/customers`;

    private readonly _http = inject(HttpClient);

    getCustomerNotes$(customerId: string, entityId: string): Observable<GetCustomerNotesResponse> {
        const params = buildHttpParams({ entity_id: entityId });
        return this._http.get<GetCustomerNotesResponse>(`${this.CUSTOMERS_API}/${customerId}/notes`, { params });
    }

    putCustomerNote(customerId: string, noteId: string, putCustomerNote: PutCustomerNote, entityId: string): Observable<void> {
        const params = buildHttpParams({ entity_id: entityId });
        return this._http.put<void>(`${this.CUSTOMERS_API}/${customerId}/notes/${noteId}`, putCustomerNote, { params });
    }

    postCustomerNote(customerId: string, postCustomerNote: PostCustomerNote, entityId: string): Observable<{ id: number }> {
        const params = buildHttpParams({ entity_id: entityId });
        return this._http.post<{ id: number }>(`${this.CUSTOMERS_API}/${customerId}/notes`, postCustomerNote, { params });
    }

    deleteCustomerNote(customerId: string, noteId: string, entityId: string): Observable<void> {
        const params = buildHttpParams({ entity_id: entityId });
        return this._http.delete<void>(`${this.CUSTOMERS_API}/${customerId}/notes/${noteId}`, { params });
    }
}
