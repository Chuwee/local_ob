import { Provider } from '@angular/core';
import { ChannelsExtendedApi } from './api/channels-extended.api';
import { ChannelsExtendedService } from './channels-extended.service';
import { ChannelsExtendedState } from './state/channels-extended.state';

export const channelsExtendedProviders: Provider[] = [
    ChannelsExtendedApi, ChannelsExtendedState, ChannelsExtendedService
];
