import { ChannelBuild } from './channel-build.model';
import { ChannelStatus } from './channel-status.model';
import { ChannelType } from './channel-type.model';
import { ChannelInvitationsSettings } from './invitations-options';
import { WhitelabelType } from './whitelabel-type.model';

export type DonationType = 'ROUND_UP' | 'CUSTOM' | 'UNRESTRICTED';

export interface IdName {
    id?: number;
    name?: string;
}

export interface Currency {
    id?: number;
    code?: string;
    name?: string;
    symbol?: string;
}

export interface Channel {
    id?: number;
    name?: string;
    public?: boolean;
    reference?: string;
    entity?: {
        id?: number;
        name?: string;
        logo?: string;
    };
    producer?: IdName;
    type?: ChannelType;
    status?: ChannelStatus;
    build?: ChannelBuild;
    domain?: string;
    url?: string;
    whitelabel_type?: WhitelabelType;
    contact?: {
        name?: string;
        surname?: string;
        email?: string;
        phone?: string;
        web?: string;
        job_position?: string;
        entity?: {
            owner?: string;
            manager?: string;
        };
    };
    languages?: {
        selected?: string[];
        default?: string;
    };
    settings?: {
        use_multi_event?: boolean;
        use_currency_exchange?: boolean;
        currency_default_exchange?: string;
        automatic_seat_selection?: boolean;
        allow_download_passbook?: boolean;
        // eslint-disable-next-line @typescript-eslint/naming-convention
        allow_B2B_publishing?: boolean;
        // eslint-disable-next-line @typescript-eslint/naming-convention
        enable_B2B_event_category_filter?: boolean;
        enable_packs_and_events_catalog?: boolean;
        enable_b2b?: boolean;
        customer_assignation?: {
            enabled?: boolean;
            mode?: 'OPTIONAL' | 'REQUIRED';
        };
        surcharges?: {
            calculation?: SurchargeMode;
        };
        invitations?: ChannelInvitationsSettings;
        allow_data_protection_fields?: boolean;
        allow_linked_customers?: boolean;
        use_robot_indexation?: boolean;
        robots_no_follow?: boolean;
        v4_config_enabled?: boolean;
        v4_enabled?: boolean;
        v4_preview_token?: string;
        v2_receipt_template_enabled?: boolean;
        donations?: {
            enabled?: boolean;
            provider?: {
                id?: number;
                target_id?: string;
            };
            settings?: {
                options?: number[];
                type?: DonationType;
            };
        };
        support_email?: {
            enabled: boolean;
            address: string;
        };
        whatsapp?: {
            override_entity_config?: boolean;
            whatsapp_template?: number;
        };
        destination_channel?: {
            destination_channel_id?: string;
            destination_channel_type?: string;
        };
    };
    limits?: {
        tickets: {
            purchase_max: number;
            preselected_items?: number;
            booking_max?: number;
            issue_max?: number;
        };
    };
    virtual_queue?: {
        active: boolean;
        alias: string;
    };
    currencies?: Currency[];
}

export enum SurchargeMode {
    beforeChannelPromotion = 'BEFORE_CHANNEL_PROMOTIONS',
    afterChannelPromotion = 'AFTER_CHANNEL_PROMOTIONS'
}
