import { StateManager } from '@OneboxTM/utils-state';
import { inject, Injectable } from '@angular/core';
import { MatcherSettingsApi } from './api/matcher-settings.api';
import { PutMatcherConfigurationRequest } from './models/matcher-configuration.model';
import { MatcherSettingsState } from './state/matcher-settings.state';

@Injectable()
export class MatcherSettingsService {
    private readonly _matcherSettingsApi = inject(MatcherSettingsApi);
    private readonly _matcherSettingsState = inject(MatcherSettingsState);

    readonly matcherConfiguration = Object.freeze({
        load: (supplierId: string) => StateManager.load(
            this._matcherSettingsState.matcherConfiguration,
            this._matcherSettingsApi.getMatchingConfiguration(supplierId)
        ),
        getMatcherConfiguration$: () => this._matcherSettingsState.matcherConfiguration.getValue$(),
        updateMatcherConfiguration: (supplierId: string, matcherConfiguration: PutMatcherConfigurationRequest) => StateManager.inProgress(
                this._matcherSettingsState.matcherConfiguration,
                this._matcherSettingsApi.putMatcherConfiguration(supplierId, matcherConfiguration)
        ),
        isLoading$: () => this._matcherSettingsState.matcherConfiguration.isInProgress$()
    });
}

