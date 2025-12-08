import { StateManager } from '@OneboxTM/utils-state';
import { Injectable, inject } from '@angular/core';
import { PrefixesApi } from './api/prefixes.api';
import { PrefixesState } from './state/prefixes.state';

@Injectable({
    providedIn: 'root'
})
export class PrefixesService {
    private _api = inject(PrefixesApi);
    private _state = inject(PrefixesState);

    readonly prefixes = Object.freeze({
        load: (): void => StateManager.loadIfNull(
            this._state.prefixes,
            this._api.getPrefixes()
        ),
        get$: () => this._state.prefixes.getValue$(),
        loading$: () => this._state.prefixes.isInProgress$()
    });
}
