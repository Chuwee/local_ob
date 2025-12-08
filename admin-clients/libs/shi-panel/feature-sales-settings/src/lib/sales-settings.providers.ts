import { Provider } from '@angular/core';
import { SalesSettingsApi } from './api/sales-settings.api';
import { SalesSettingsService } from './sales-settings.service';
import { SalesSettingsState } from './state/sales-settings.state';

export const salesSettingsProviders: Provider[] = [
    SalesSettingsApi,
    SalesSettingsState,
    SalesSettingsService
];
