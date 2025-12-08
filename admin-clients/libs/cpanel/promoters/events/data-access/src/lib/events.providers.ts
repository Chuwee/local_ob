import { Provider } from '@angular/core';
import { EventsApi } from './api/events.api';
import { EventsService } from './events.service';
import { EventsState } from './state/events.state';

export const eventsProviders: Provider[] = [
    EventsApi,
    EventsState,
    EventsService
];
