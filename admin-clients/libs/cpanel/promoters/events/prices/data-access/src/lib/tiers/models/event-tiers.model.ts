import { EventTierConditions } from './event-tier-conditions.enum';

export interface EventTiers {
    id: number;
    name: string;
    active: boolean;
    limit: number;
    condition?: EventTierConditions;
    price_type: {
        id: number;
        name: string;
    };
    start_date: string;
    price: number;
    on_sale: boolean;
    quotas_limit?: {
        id: number;
        name: string;
        limit: number;
    }[];
}
