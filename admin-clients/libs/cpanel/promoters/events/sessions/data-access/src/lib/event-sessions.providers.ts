import { Provider } from '@angular/core';
import { EventSessionsApi } from './api/sessions.api';
import { EventSessionsService } from './sessions.service';
import { EventSessionsState } from './state/sessions.state';

export const eventSessionsProviders: Provider[] = [
    EventSessionsApi,
    EventSessionsState,
    EventSessionsService
];
