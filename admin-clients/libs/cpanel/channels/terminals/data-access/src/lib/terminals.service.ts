import { mapMetadata, StateManager } from '@OneboxTM/utils-state';
import { inject, Injectable, Provider } from '@angular/core';
import { TerminalsApi } from './api/terminals.api';
import { GetTerminalsRequest } from './models/get-terminals-request.model';
import { PostTerminal, PutTerminal } from './models/terminal.model';
import { TerminalsState } from './state/terminals.state';

export const terminalsProviders = (): Provider[] => [TerminalsApi, TerminalsState, TerminalsService];

@Injectable()
export class TerminalsService {

    private readonly _state = inject(TerminalsState);
    private readonly _api = inject(TerminalsApi);

    readonly terminals = Object.freeze({
        load: (req: GetTerminalsRequest) => StateManager.load(this._state.terminals, this._api.getTerminals(req).pipe(mapMetadata())),
        inProgress$: () => this._state.terminals.isInProgress$(),
        get$: () => this._state.terminals.getValue$(),
        clear: () => this._state.terminals.setValue(null)
    });

    readonly terminal = Object.freeze({
        load: (id: number) => StateManager.load(this._state.terminal, this._api.getTerminal(id)),
        regenerateLicense: (id: number) => StateManager.inProgress(this._state.terminal, this._api.regenerateLicense(id)),
        save: (id: number, put: PutTerminal) => StateManager.inProgress(this._state.terminal, this._api.putTerminal(id, put)),
        create: (post: PostTerminal) => StateManager.inProgress(this._state.terminal, this._api.postTerminal(post)),
        delete: (id: number) => StateManager.inProgress(this._state.terminal, this._api.deleteTerminal(id)),
        inProgress$: () => this._state.terminal.isInProgress$(),
        get$: () => this._state.terminal.getValue$(),
        error$: () => this._state.terminal.getError$(),
        clear: () => this._state.terminal.setValue(null)
    });
}
