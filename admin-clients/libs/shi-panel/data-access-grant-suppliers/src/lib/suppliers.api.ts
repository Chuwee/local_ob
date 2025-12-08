import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { GetSuppliersResponse } from './models/get-suppliers-response.model';

@Injectable()
export class SuppliersApi {
    private readonly BASE_SUPPLIERS_URL = '/api/shi-mgmt-api/v1/suppliers';
    private readonly _http = inject(HttpClient);

    getSuppliers(): Observable<GetSuppliersResponse> {
        return this._http.get<GetSuppliersResponse>(`${this.BASE_SUPPLIERS_URL}`);
    }
}
