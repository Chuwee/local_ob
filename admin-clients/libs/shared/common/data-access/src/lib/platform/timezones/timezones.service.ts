import { StateManager } from '@OneboxTM/utils-state';
import { inject, Injectable } from '@angular/core';
import { TimezonesApi } from './api/timezones.api';
import { TimezonesState } from './state/timezones.state';

@Injectable({
    providedIn: 'root'
})
export class TimezonesService {
    private readonly _api = inject(TimezonesApi);
    private readonly _state = inject(TimezonesState);

    readonly timezones = Object.freeze({
        load: (): void => StateManager.loadIfNull(
            this._state.timezones,
            this._api.getTimezones()
        ),
        get$: () => this._state.timezones.getValue$(),
        loading$: () => this._state.timezones.isInProgress$()
    });
}
