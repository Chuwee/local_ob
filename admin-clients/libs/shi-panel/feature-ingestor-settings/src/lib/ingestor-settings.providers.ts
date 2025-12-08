import { Provider } from '@angular/core';
import { IngestorSettingsApi } from './api/ingestor-settings.api';
import { IngestorSettingsService } from './ingestor-settings.service';
import { IngestorSettingsState } from './state/ingestor-settings.state';

export const ingestorSettingsProviders: Provider[] = [
    IngestorSettingsApi,
    IngestorSettingsState,
    IngestorSettingsService
];
