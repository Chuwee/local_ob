import { Provider } from '@angular/core';
import { MatcherSettingsApi } from './api/matcher-settings.api';
import { MatcherSettingsService } from './matcher-settings.service';
import { MatcherSettingsState } from './state/matcher-settings.state';

export const matcherSettingsProviders: Provider[] = [
    MatcherSettingsApi,
    MatcherSettingsState,
    MatcherSettingsService
];
