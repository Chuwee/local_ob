import { StateManager } from '@OneboxTM/utils-state';
import { inject, Injectable } from '@angular/core';
import { SalesSettingsApi } from './api/sales-settings.api';
import { PatchShiConfiguration } from './models/sales-configuration.model';
import { SalesSettingsState } from './state/sales-settings.state';

@Injectable()
export class SalesSettingsService {
    readonly #salesSettingsApi = inject(SalesSettingsApi);
    readonly #salesSettingsState = inject(SalesSettingsState);

    readonly salesConfiguration = Object.freeze({
        load: () => StateManager.load(
            this.#salesSettingsState.salesConfiguration,
            this.#salesSettingsApi.getSalesConfiguration()
        ),
        getSalesConfiguration$: () => this.#salesSettingsState.salesConfiguration.getValue$(),
        updateSalesConfiguration: (salesConfiguration: PatchShiConfiguration) => StateManager.inProgress(
            this.#salesSettingsState.salesConfiguration,
            this.#salesSettingsApi.patchSalesConfiguration(salesConfiguration)
        ),
        isInProgress$: () => this.#salesSettingsState.salesConfiguration.isInProgress$()
    });
}

