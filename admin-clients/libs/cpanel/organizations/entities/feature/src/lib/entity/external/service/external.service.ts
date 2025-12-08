import { StateManager } from '@OneboxTM/utils-state';
import { Injectable, inject } from '@angular/core';
import { finalize } from 'rxjs';
import { ExternalEntitiesApi } from '../api/external.api';
import { ExternalEntityConfiguration } from '../models/configuration.model';
import { ExternalEntitiesState } from './external.state';

@Injectable({
    providedIn: 'root'
})
export class ExternalEntityService {
    private readonly _api = inject(ExternalEntitiesApi);
    private readonly _state = inject(ExternalEntitiesState);

    readonly capacities = Object.freeze({
        reload: (entityId: number) => StateManager.load(
            this._state.capacities,
            this._api.getExternalCapacities(entityId)
        ),
        load: (entityId: number) => StateManager.loadIfNull(
            this._state.capacities,
            this._api.getExternalCapacities(entityId)
        ),
        clear: () => this._state.capacities.setValue(null),
        loading$: () => this._state.capacities.isInProgress$(),
        get$: () => this._state.capacities.getValue$(),
        import: (entityId: number, id: number) => StateManager.inProgress(
            this._state.capacities,
            this._api.importExternalCapacity(entityId, id)
        ),
        delete: (entityId: number, id: number) => StateManager.inProgress(
            this._state.capacities,
            this._api.deleteExternalCapacity(entityId, id)
        ),
        refresh: (entityId: number, id: number) => StateManager.inProgress(
            this._state.capacities,
            this._api.postRefreshExternalCapacity(entityId, id)
        ),
        mapping: (entityId: number, id: number) => StateManager.inProgress(
            this._state.capacities,
            this._api.postMappingExternalCapacity(entityId, id)
        )
    });

    readonly configuration = Object.freeze({
        reload: (id: number) => StateManager.load(this._state.configuration, this._api.getConfiguration(id)),
        load: (id: number) => StateManager.loadIfNull(this._state.configuration, this._api.getConfiguration(id)),
        clear: () => this._state.configuration.setValue(null),
        save: (id: number, configuration: Partial<ExternalEntityConfiguration>) =>
            StateManager.inProgress(
                this._state.configuration,
                this._api.putConfiguration(id, configuration)
            ).pipe(
                finalize(() => this.configuration.load(id))
            ),
        get$: () => this._state.configuration.getValue$(),
        loading$: () => this._state.configuration.isInProgress$()
    });

    readonly clubCodes = Object.freeze({
        reload: () => StateManager.load(this._state.clubCodes, this._api.getClubCodes()),
        load: () =>
            StateManager.loadIfNull(this._state.clubCodes, this._api.getClubCodes()),
        clear: () => this._state.clubCodes.setValue(null),
        link: (id: number, code: string) =>
            StateManager.inProgress(
                this._state.link,
                this._api.linkClubCode(id, code)
            ),
        unlink: (id: number) =>
            StateManager.inProgress(
                this._state.link,
                this._api.unlinkClubCode(id)
            ),
        get$: () => this._state.clubCodes.getValue$(),
        loading$: () => this._state.clubCodes.isInProgress$(),
        linking$: () => this._state.link.isInProgress$()
    });

    readonly periodicities = Object.freeze({
        reload: (entityId: number) => StateManager.load(
            this._state.periodicities,
            this._api.getExternalPeriodicities(entityId)
        ),
        load: (entityId: number) => StateManager.loadIfNull(
            this._state.periodicities,
            this._api.getExternalPeriodicities(entityId)
        ),
        clear: () => this._state.periodicities.setValue(null),
        loading$: () => this._state.periodicities.isInProgress$(),
        get$: () => this._state.periodicities.getValue$()
    });

    readonly roles = Object.freeze({
        reload: (entityId: number) => StateManager.load(
            this._state.roles,
            this._api.getExternalRoles(entityId)
        ),
        load: (entityId: number) => StateManager.loadIfNull(
            this._state.roles,
            this._api.getExternalRoles(entityId)
        ),
        clear: () => this._state.roles.setValue(null),
        loading$: () => this._state.roles.isInProgress$(),
        get$: () => this._state.roles.getValue$()
    });

    readonly terms = Object.freeze({
        reload: (entityId: number) => StateManager.load(
            this._state.terms,
            this._api.getExternalTerms(entityId)
        ),
        load: (entityId: number) => StateManager.loadIfNull(
            this._state.terms,
            this._api.getExternalTerms(entityId)
        ),
        clear: () => this._state.terms.setValue(null),
        loading$: () => this._state.terms.isInProgress$(),
        get$: () => this._state.terms.getValue$()
    });

    readonly inventories = Object.freeze({
        reload: (entityId: number, providerId: string, req?: { skip_used: boolean }) =>
            StateManager.load(this._state.inventories, this._api.getExternalInventories(entityId, providerId, req)),
        load: (entityId: number, providerId: string, req?: { skip_used: boolean }) =>
            StateManager.loadIfNull(this._state.inventories, this._api.getExternalInventories(entityId, providerId, req)),
        clear: () => this._state.inventories.setValue(null),
        get$: () => this._state.inventories.getValue$(),
        loading$: () => this._state.inventories.isInProgress$()
    });

    readonly inventoryProviders = Object.freeze({
        reload: (id: number) => StateManager.load(this._state.inventoryProviders, this._api.getInventoryProviders(id)),
        load: (id: number) => StateManager.loadIfNull(this._state.inventoryProviders, this._api.getInventoryProviders(id)),
        clear: () => this._state.inventoryProviders.setValue(null),
        get$: () => this._state.inventoryProviders.getValue$(),
        loading$: () => this._state.inventoryProviders.isInProgress$()
    });

}
