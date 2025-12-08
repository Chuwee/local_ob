import { StateProperty } from '@OneboxTM/utils-state';
import type { Channel } from '@admin-clients/cpanel/channels/data-access';
import type { DomainConfiguration, DomainSettings } from '@admin-clients/cpanel/shared/data-access';
import type { CorsSettings, GetAdminChannelsResponse } from './models/admin-channels.model';

export class AdminChannelsState {
    readonly adminChannelsList = new StateProperty<GetAdminChannelsResponse>();
    readonly channel = new StateProperty<Channel>();
    readonly channelCorsSettings = new StateProperty<CorsSettings>();
    readonly channelSubdomainSettings = new StateProperty<DomainSettings>();
    readonly domainConfiguration = new StateProperty<DomainConfiguration>();
}
