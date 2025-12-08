import { Provider } from '@angular/core';
import { EventRestrictionsApi } from './api/event-restrictions.api';
import { EventRestrictionsService } from './event-restrictions.service';
import { EventRestrictionsState } from './state/event-restrictions.state';

export const eventRestrictionsProviders: Provider[] = [
    EventRestrictionsApi,
    EventRestrictionsState,
    EventRestrictionsService
];
