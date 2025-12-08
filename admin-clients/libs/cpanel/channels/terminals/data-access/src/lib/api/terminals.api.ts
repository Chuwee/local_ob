import { buildHttpParams } from '@OneboxTM/utils-http';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { Id } from '@admin-clients/shared/data-access/models';
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GetTerminalsRequest } from '../models/get-terminals-request.model';
import { GetTerminalsResponse } from '../models/get-terminals-response.model';
import { PostTerminal, PutTerminal, Terminal } from '../models/terminal.model';

@Injectable()
export class TerminalsApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly TERMINALS_API = `${this.BASE_API}/mgmt-api/v1/terminals`;

    private _http = inject(HttpClient);

    getTerminals(request: GetTerminalsRequest): Observable<GetTerminalsResponse> {
        return this._http.get<GetTerminalsResponse>(this.TERMINALS_API, { params: buildHttpParams(request) });
    }

    getTerminal(id: number): Observable<Terminal> {
        return this._http.get<Terminal>(`${this.TERMINALS_API}/${id}`);
    }

    putTerminal(id: number, putTerminal: PutTerminal): Observable<unknown> {
        return this._http.put(`${this.TERMINALS_API}/${id}`, putTerminal);
    }

    deleteTerminal(id: number): Observable<unknown> {
        return this._http.delete(`${this.TERMINALS_API}/${id}`);
    }

    postTerminal(postTerminal: PostTerminal): Observable<Id> {
        return this._http.post<Id>(this.TERMINALS_API, postTerminal);
    }

    regenerateLicense(id: number): Observable<unknown> {
        return this._http.post<unknown>(`${this.TERMINALS_API}/${id}/regenerate-license`, null);
    }
}
