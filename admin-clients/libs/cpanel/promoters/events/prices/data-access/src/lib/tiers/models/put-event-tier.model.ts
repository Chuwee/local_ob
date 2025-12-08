import { EventTierConditions } from './event-tier-conditions.enum';

export interface PutEventTier {
    name?: string;
    start_date?: string;
    price?: number;
    on_sale?: boolean;
    limit?: number;
    condition?: EventTierConditions;
}

