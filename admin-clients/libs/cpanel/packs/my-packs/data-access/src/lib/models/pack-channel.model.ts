import { ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { PackChannelRequestStatus } from './pack-channel-request-status.enum';

export type PackChannel = {
    pack: {
        id: number;
        name: string;
        status: 'ACTIVE' | 'INACTIVE';
    };
    channel: {
        id: number;
        name: string;
        type: ChannelType;
        entity: {
            id: number;
            name: string;
            logo: string;
        };
    };
    status: {
        request: PackChannelRequestStatus;
    };
    settings: {
        suggested: boolean;
        on_sale_for_logged_users: boolean;
    };
};

export type PutPackChannel = {
    suggested: boolean;
};