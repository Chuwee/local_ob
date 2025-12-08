import { StateManager } from '@OneboxTM/utils-state';
import { inject, Injectable } from '@angular/core';
import { GoogleCloudApi } from './api/google-cloud.api';
import { GoogleCloudState } from './state/google-cloud.state';

@Injectable({ providedIn: 'root' })
export class GoogleCloudService {
    readonly #api = inject(GoogleCloudApi);
    readonly #googleCloudState = inject(GoogleCloudState);

    readonly placesLibrary = Object.freeze({
        load: () => StateManager.load(this.#googleCloudState.placesLibrary, this.#api.getPlacesLibrary$()),
        get$: () => this.#googleCloudState.placesLibrary.getValue$(),
        isInProgress$: () => this.#googleCloudState.placesLibrary.isInProgress$(),
        clear: () => this.#googleCloudState.placesLibrary.setValue(null)
    });

    readonly timezone = Object.freeze({
        load: (latitude: string, longitude: string) => StateManager.load(
            this.#googleCloudState.timezone,
            this.#api.getMapsTimezone$(latitude, longitude)
        ),
        get$: () => this.#googleCloudState.timezone.getValue$(),
        isInProgress$: () => this.#googleCloudState.timezone.isInProgress$(),
        clear: () => this.#googleCloudState.timezone.setValue(null)
    });
}
