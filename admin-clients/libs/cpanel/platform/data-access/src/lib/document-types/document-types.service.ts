import { StateManager } from '@OneboxTM/utils-state';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { DocumentTypesApi } from './api/document-types.api';
import { DocumentTypesState } from './state/document-types.state';

@Injectable({ providedIn: 'any' })
export class DocumentTypesService {

    private readonly _state = inject(DocumentTypesState);
    private readonly _api = inject(DocumentTypesApi);

    loadDocumentTypes(entityId: number): void {
        StateManager.load(this._state.docTypes, this._api.getDocumentTypes(entityId));
    }

    getDocTypes$(): Observable<string[]> {
        return this._state.docTypes.getValue$();
    }

    isDocTypesLoading$(): Observable<boolean> {
        return this._state.docTypes.isInProgress$();
    }
}
