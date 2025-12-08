import { ListResponse } from '@OneboxTM/utils-state';
import { ChannelStatus, ChannelType, WhitelabelType } from '@admin-clients/cpanel/channels/data-access';

export interface GetAdminChannelsResponse extends ListResponse<AdminChannel> { }

export type AdminChannel = {
    id: number;
    name: string;
    status: ChannelStatus;
    entity: {
        id: number;
        name: string;
        logo?: string;
    };
    type: ChannelType;
    whitelabel_settings?: {
        v4_enabled?: boolean;
        v4_config_enabled?: boolean;
        v2_receipt_template_enabled?: boolean;
        whitelabelType?: WhitelabelType;
    };
};

export type PostChannelMigrateRequest = {
    migrate_to_channels: boolean;
    stripe_hook_checked: boolean;
};

export type PutReceiptMigrateRequest = {
    migrate_receipt_template: boolean;
};

export type PutChannelWhitelabelType = {
    whitelabel_type: WhitelabelType;
};

export type CorsSettings = {
    enabled: boolean;
    allowed_origins: string[];
};
