import { StateManager } from '@OneboxTM/utils-state';
import { Injectable, inject } from '@angular/core';
import { PromotersExternalProviderApi } from './api/external-provider.api';
import { ExternalProviderEventsQuery } from './models/external-provider-events.model';
import { ExternalProviderSessionsPresalesQuery } from './models/external-provider-presales.model';
import { ExternalProviderSessionsQuery } from './models/external-provider-sessions.model';
import { PromotersExternalProviderState } from './state/external-provider.state';

@Injectable({
    providedIn: 'root'
})
export class PromotersExternalProviderService {
    private readonly _api = inject(PromotersExternalProviderApi);
    private readonly _state = inject(PromotersExternalProviderState);

    readonly providerEvents = Object.freeze({
        reload: (req: ExternalProviderEventsQuery) =>
            StateManager.load(this._state.providerEvents, this._api.getExternalProviderEvents(req)),
        load: (req: ExternalProviderEventsQuery) =>
            StateManager.loadIfNull(this._state.providerEvents, this._api.getExternalProviderEvents(req)),
        clear: () => this._state.providerEvents.setValue(null),
        get$: () => this._state.providerEvents.getValue$(),
        loading$: () => this._state.providerEvents.isInProgress$()
    });

    readonly providerSessions = Object.freeze({
        reload: (req: ExternalProviderSessionsQuery) =>
            StateManager.load(this._state.providerSessions, this._api.getExternalProviderSessions(req)),
        load: (req: ExternalProviderSessionsQuery) =>
            StateManager.loadIfNull(this._state.providerSessions, this._api.getExternalProviderSessions(req)),
        clear: () => this._state.providerSessions.setValue(null),
        get$: () => this._state.providerSessions.getValue$(),
        loading$: () => this._state.providerSessions.isInProgress$()
    });

    readonly providerSessionsPresales = Object.freeze({
        reload: (req: ExternalProviderSessionsPresalesQuery) =>
            StateManager.load(this._state.providerSessionsPresales, this._api.getExternalProviderSessionsPresales(req)),
        load: (req: ExternalProviderSessionsPresalesQuery) =>
            StateManager.loadIfNull(this._state.providerSessionsPresales, this._api.getExternalProviderSessionsPresales(req)),
        clear: () => this._state.providerSessionsPresales.setValue(null),
        get$: () => this._state.providerSessionsPresales.getValue$(),
        loading$: () => this._state.providerSessionsPresales.isInProgress$()
    });

    readonly providerSeasonTicketsPresales = Object.freeze({
        reload: (seasonTicketId: number, skipUsed?: boolean) =>
            StateManager.load(
                this._state.providerSeasonTicketsPresales,
                this._api.getExternalProviderSeasonTicketsPresales(seasonTicketId, skipUsed)
            ),
        load: (seasonTicketId: number, skipUsed?: boolean) =>
            StateManager.loadIfNull(
                this._state.providerSeasonTicketsPresales,
                this._api.getExternalProviderSeasonTicketsPresales(seasonTicketId, skipUsed)
            ),
        clear: () => this._state.providerSeasonTicketsPresales.setValue(null),
        get$: () => this._state.providerSeasonTicketsPresales.getValue$(),
        loading$: () => this._state.providerSeasonTicketsPresales.isInProgress$()
    });
}
