import { StateManager } from '@OneboxTM/utils-state';
import { inject, Injectable } from '@angular/core';
import { IngestorSettingsApi } from './api/ingestor-settings.api';
import { PutIngestorConfigurationRequest } from './models/ingestor-configuration.model';
import { IngestorSettingsState } from './state/ingestor-settings.state';

@Injectable()
export class IngestorSettingsService {
    readonly #ingestorSettingsApi = inject(IngestorSettingsApi);
    readonly #ingestorSettingsState = inject(IngestorSettingsState);

    readonly ingestorConfiguration = Object.freeze({
        load: (supplierId: string) => StateManager.load(
            this.#ingestorSettingsState.ingestorConfiguration,
            this.#ingestorSettingsApi.getIngestorConfiguration(supplierId)
        ),
        getIngestorConfiguration$: () => this.#ingestorSettingsState.ingestorConfiguration.getValue$(),
        updateIngestorConfiguration: (
            supplierId: string, ingestorConfiguration: PutIngestorConfigurationRequest
        ) => StateManager.inProgress(
                this.#ingestorSettingsState.ingestorConfiguration,
                this.#ingestorSettingsApi.putIngestorConfiguration(supplierId, ingestorConfiguration)
        ),
        isInProgress$: () => this.#ingestorSettingsState.ingestorConfiguration.isInProgress$()
    });
}

