import { ChannelType, WhitelabelType } from '@admin-clients/cpanel/channels/data-access';
import { EventChannelReleaseStatus } from './event-channel-release-status.enum';
import { EventChannelRequestStatus } from './event-channel-request-status.enum';
import { EventChannelSaleStatus } from './event-channel-sale-status.enum';

export interface EventChannel {
    channel: {
        id: number;
        name: string;
        type: ChannelType;
        is_v4?: boolean;
        favorite: boolean;
        whitelabel_type?: WhitelabelType;
        force_square_pictures?: boolean;
        entity: {
            id: number;
            name: string;
            logo: string;
        };
    };
    status: {
        request: EventChannelRequestStatus;
        sale: EventChannelSaleStatus;
        release: EventChannelReleaseStatus;
    };
    event: {
        id: number;
        status: string;
    };
    settings: {
        use_event_dates: boolean;
        secondary_market_enabled: boolean;
        release: {
            enabled: boolean;
            date: string;
        };
        sale: {
            enabled: boolean;
            start_date: string;
            end_date: string;
        };
        booking: {
            enabled: boolean;
            start_date: string;
            end_date: string;
        };
        secondary_market_sale?: {
            enabled?: boolean;
            start_date?: string;
            end_date?: string;
        };
        languages: {
            default: string;
            selected: [
                string
            ];
        };
    };
    use_all_quotas: boolean;
    quotas: EventChannelQuota[];
    parsedQuotas?: {
        template_id: number;
        template_name: string;
        selected: boolean;
        quotaActive: number;
        quotas: {
            description: string;
            id: number;
        }[];
    }[];
}

export interface EventChannelQuota {
    id: number;
    description: string;
    template_name: string;
    template_id: number;
    selected: boolean;
}
