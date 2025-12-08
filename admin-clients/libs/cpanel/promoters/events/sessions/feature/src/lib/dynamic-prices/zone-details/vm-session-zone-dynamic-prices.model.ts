import { SessionZoneDynamicPriceConditionType } from '@admin-clients/cpanel/promoters/events/sessions/data-access';

export interface VmSessionZoneDynamicPrice {
    name: string;
    capacity?: number;
    valid_date?: string;
    condition_types: SessionZoneDynamicPriceConditionType[];
    status_dynamic_price: 'ACTIVE' | 'PENDING' | 'BLOCKED' | 'COMPLETE';
    order: number;
    dynamic_rates_price: {
        id?: number;
        name: string;
        price: number;
    }[];
    translations: {
        language: string;
        value: string;
    }[];
}
