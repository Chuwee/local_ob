import { StateManager } from '@OneboxTM/utils-state';
import { inject, Injectable } from '@angular/core';
import { GatewaysApi } from './api/gateways.api';
import { GatewaysState } from './state/gateways.state';

@Injectable({
    providedIn: 'root'
})
export class GatewaysService {
    private readonly _api = inject(GatewaysApi);
    private readonly _state = inject(GatewaysState);

    readonly gatewaysList = Object.freeze({
        load: (): void => StateManager.loadIfNull(
            this._state.gatewaysList,
            this._api.getGateways()
        ),
        get$: () => this._state.gatewaysList.getValue$(),
        loading$: () => this._state.gatewaysList.isInProgress$()
    });

    readonly gateway = Object.freeze({
        load: (gatewayId: string): void => StateManager.load(
            this._state.gateway,
            this._api.getGateway(gatewayId)
        ),
        get$: () => this._state.gateway.getValue$(),
        loading$: () => this._state.gateway.isInProgress$(),
        clear: () => this._state.gateway.setValue(null)
    });
}
