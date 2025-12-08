import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { buildHttpParams } from '@OneboxTM/utils-http';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'any' })
export class DocumentTypesApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly DOCUMENT_TYPES_API = `${this.BASE_API}/mgmt-api/v1/document-types`;

    private readonly _http = inject(HttpClient);

    getDocumentTypes(entityId: number): Observable<string[]> {
        return this._http.get<string[]>(this.DOCUMENT_TYPES_API, { params: buildHttpParams({ entity_id: entityId }) });
    }
}
