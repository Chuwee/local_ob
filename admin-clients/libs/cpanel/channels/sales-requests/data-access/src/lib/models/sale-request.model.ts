import { ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { TaxesMode } from '@admin-clients/cpanel-promoters-events-prices-data-access';
import { EventType } from '@admin-clients/shared/common/data-access';
import { SalesRequestsStatus } from './sales-requests-status.model';

export interface SaleRequest {
    id: number;
    date: string;
    status: SalesRequestsStatus;
    languages: {
        default: string;
        selected: string[];
    };
    channel: {
        id: number;
        name: string;
        type?: ChannelType;
        entity: {
            id: number;
            name: string;
        };
        category?: {
            parent?: {
                id: number;
                code: string;
                description: string;
            };
            custom?: {
                id: number;
                code: string;
                description: string;
            };
        };
    };
    event: {
        id: number;
        currency_code: string;
        event_type: EventType;
        name: string;
        entity: {
            id: number;
            name: string;
        };
        venues: [
            {
                id: number;
                name: string;
                location: {
                    city: string;
                };
            }
        ];
        category?: {
            id: number;
            code: string;
            description: string;
            parent?: {
                id: number;
                code: string;
                description: string;
            };
            custom?: {
                id: number;
                code: string;
                description: string;
            };
        };
        contact_person: {
            name: string;
            surname: string;
            email: string;
            phone?: string;
        };
        tax_mode?: TaxesMode;
    };
    subscription_list?: {
        enable: boolean;
        id?: number;
    };
}
