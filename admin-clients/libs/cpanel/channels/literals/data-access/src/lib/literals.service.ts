import { StateManager } from '@OneboxTM/utils-state';
import { inject, Injectable } from '@angular/core';
import { map } from 'rxjs';
import { LiteralsApi } from './api/literals.api';
import { ChannelLiteralsTextContent } from './models/post-literals.model';
import { LiteralsState } from './state/literals.state';

@Injectable({
    providedIn: 'root'
})
export class LiteralsService {

    private readonly _api = inject(LiteralsApi);
    private readonly _state = inject(LiteralsState);

    readonly literals = Object.freeze({
        load: (appName: string, language: string) => StateManager.load(
            this._state.literals,
            this._api.getApplicationTextContents(appName, language).pipe(
                map(contents => contents.sort((a, b) => a.key.localeCompare(b.key)))
            )
        ),
        get$: () => this._state.literals.getValue$(),
        loading$: () => this._state.literals.isInProgress$(),
        create: (appName: string, language: string, payload: ChannelLiteralsTextContent[]) => StateManager.inProgress(
            this._state.literals,
            this._api.postApplicationTextContents(appName, language, payload)
        ),
        clear: () => this._state.literals.setValue(null)
    });

}
