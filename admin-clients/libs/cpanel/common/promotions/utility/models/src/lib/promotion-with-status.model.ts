import { PromotionStatus } from './promotion-status.enum';
import { PromotionValidityPeriodType } from './promotion-validity-period-type.enum';

export interface PromotionWithStatusAndValidity {
    status?: PromotionStatus;
    validity_period?: {
        type: PromotionValidityPeriodType;
        start_date?: string;
        end_date?: string;
    };
    dates?: {
        start?: string;
        end?: string;
    };
}
