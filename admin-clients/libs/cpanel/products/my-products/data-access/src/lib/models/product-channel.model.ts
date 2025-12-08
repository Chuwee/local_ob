import { ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { IdName } from '@admin-clients/shared/data-access/models';

export type ProductChannel = {
    product: IdName;
    channel: Partial<{
        id: number;
        name: string;
        entity: {
            id: number;
            name: string;
            logo: string;
        };
        type: ChannelType;
    }>;
    checkout_suggestion_enabled: boolean;
    standalone_enabled: boolean;
    sale_request_status: ProductChannelSaleRequestStatus;
    languages?: {
        selected: string[];
        default: string;
    };
};

export type ProductChannelSaleRequestStatus = 'PENDING_REQUEST' | 'REJECTED' | 'PENDING' | 'ACCEPTED';
export type ProductChannelStatusIndicator = 'neutral' | 'pending' | 'rejected' | 'success';
