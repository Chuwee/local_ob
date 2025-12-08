import { ChannelType } from '@admin-clients/cpanel/channels/data-access';

export interface RateRestrictionsChannel {
    channel: {
        id: number;
        name: string;
        type?: ChannelType;
    };
}
