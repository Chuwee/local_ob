import { Provider } from '@angular/core';
import { EventChannelsApi } from './api/event-channels.api';
import { EventChannelsService } from './event-channels.service';
import { EventChannelsState } from './state/event-channels.state';

export const eventChannelsProviders: Provider[] = [
    EventChannelsApi,
    EventChannelsState,
    EventChannelsService
];
