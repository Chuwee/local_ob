import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { GetOrderNotesResponse } from '../models/get-order-notes-response.model';
import { PostOrderNote } from '../models/post-order-note.model';
import { PutOrderNote } from '../models/put-order-note.model';

@Injectable()
export class OrderNotesApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly ORDERS_API = `${this.BASE_API}/orders-mgmt-api/v1/orders`;

    private readonly _http = inject(HttpClient);

    getOrderNotes$(orderCode: string): Observable<GetOrderNotesResponse> {
        return this._http.get<GetOrderNotesResponse>(`${this.ORDERS_API}/${orderCode}/notes`);
    }

    putOrderNote(orderCode: string, noteId: string, putCustomerNote: PutOrderNote): Observable<void> {
        return this._http.put<void>(`${this.ORDERS_API}/${orderCode}/notes/${noteId}`, putCustomerNote);
    }

    postOrderNote(orderCode: string, postCustomerNote: PostOrderNote): Observable<{ id: number }> {
        return this._http.post<{ id: number }>(`${this.ORDERS_API}/${orderCode}/notes`, postCustomerNote);
    }

    deleteOrderNote(orderCode: string, noteId: string): Observable<void> {
        return this._http.delete<void>(`${this.ORDERS_API}/${orderCode}/notes/${noteId}`);
    }
}
