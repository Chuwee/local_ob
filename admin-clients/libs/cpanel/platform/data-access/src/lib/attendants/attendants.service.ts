import { getListData, getMetadata, mapMetadata, StateManager } from '@OneboxTM/utils-state';
import { Injectable, inject } from '@angular/core';
import { AttendantsServicesApi } from './api/attendants.api';
import { AttendantsState } from './state/attendants.state';

@Injectable({
    providedIn: 'root'
})
export class AttendantsService {

    readonly #api = inject(AttendantsServicesApi);
    readonly #state = inject(AttendantsState);

    readonly attendantFields = Object.freeze({
        load: () => StateManager.loadIfNull(
            this.#state.attendantFields,
            this.#api.getAttendantFields().pipe(mapMetadata())
        ),
        get$: () => this.#state.attendantFields.getValue$(),
        getData$: () => this.#state.attendantFields.getValue$().pipe(getListData()),
        getMetadata$: () => this.#state.attendantFields.getValue$().pipe(getMetadata()),
        loading$: () => this.#state.attendantFields.isInProgress$(),
        clear: () => this.#state.attendantFields.setValue(null)
    });
}
