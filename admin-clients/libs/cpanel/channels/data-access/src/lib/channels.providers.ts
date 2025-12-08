import { Provider } from '@angular/core';
import { ChannelsApi } from './api/channels.api';
import { ChannelsService } from './channels.service';
import { ChannelsState } from './state/channels.state';

export const channelsProviders: Provider[] = [
    ChannelsApi, ChannelsState, ChannelsService
];
