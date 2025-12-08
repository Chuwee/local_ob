import { ListResponse } from '@OneboxTM/utils-state';
import { PromotionDiscountType, PromotionStatus, PromotionType } from '@admin-clients/cpanel-common-promotions-utility-models';
import { SaleRequestPromotionValidityPeriodType } from './sale-request-promotion-validity-period-type.enum';

export interface GetSaleRequestPromotionsResponse extends ListResponse<SaleRequestPromotion> {
}

export interface SaleRequestPromotion {
    id: number;
    name: string;
    status: PromotionStatus;
    type: PromotionType;
    price_variation: {
        type: PromotionDiscountType;
        value?: number;
        ranges?: {
            from: number;
            value: number;
        }[];
    };
    collective?: {
        id: number;
        name: string;
    };
    restrictive_access: boolean;
    validity_period: {
        type: SaleRequestPromotionValidityPeriodType;
        from?: string;
        to?: string;
    };
    sessions?: {
        id: number;
        name: string;
        date: {
            start: string;
            end: string;
            publication: string;
            sales_start: string;
            sales_end: string;
            booking_start: string;
            booking_end: string;
        };
    }[];
    price_types?: {
        id: number;
        name: string;
        venue_template: {
            id: number;
            name: string;
        };
    }[];
    rates?: {
        id: number;
        name: string;
    }[];
}
