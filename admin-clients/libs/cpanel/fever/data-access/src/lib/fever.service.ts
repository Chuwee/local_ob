import { getListData, StateManager } from '@OneboxTM/utils-state';
import { inject, Injectable } from '@angular/core';
import { FeverApi } from './api/fever.api';
import { FEVER_ZONE_URL } from './fever-zone.token';
import { FeverState } from './state/fever.state';

const FEVER_LOGIN_PATH = 'login/onebox';

@Injectable({ providedIn: 'root' })
export class FeverService {
    readonly #api = inject(FeverApi);
    readonly #state = inject(FeverState);
    readonly #feverZoneUrl = inject(FEVER_ZONE_URL, { optional: true }) || '';

    readonly entites = Object.freeze({
        load: () => StateManager.load(
            this.#state.entities,
            this.#api.getEntities().pipe(getListData())
        ),
        get$: () => this.#state.entities.getValue$(),
        error$: () => this.#state.entities.getError$(),
        loading$: () => this.#state.entities.isInProgress$(),
        clear: () => this.#state.entities.setValue(null)
    });

    readonly destinationChannels = Object.freeze({
        load: (entityId: number, type: string) => StateManager.load(
            this.#state.destinationChannels,
            this.#api.getDestinationChannels(entityId, type)
        ),
        get$: () => this.#state.destinationChannels.getValue$(),
        error$: () => this.#state.destinationChannels.getError$(),
        loading$: () => this.#state.destinationChannels.isInProgress$(),
        clear: () => this.#state.destinationChannels.setValue(null)
    });

    loginUrl(entityId: number, token: string, redirectTo?: string): string {
        const params = new URLSearchParams();
        params.set('token', token);
        params.set('entityId', entityId.toString());
        if (redirectTo) {
            params.set('redirectTo', redirectTo);
        }

        return `${this.#feverZoneUrl}/${FEVER_LOGIN_PATH}?${params.toString()}`;
    }
}
