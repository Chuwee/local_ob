import { BasePromotion, BasePromotionCollective } from '@admin-clients/cpanel/promoters/data-access';
import { PromotionStatus } from '@admin-clients/cpanel-common-promotions-utility-models';

export interface EventPromotion extends BasePromotion {
    status?: PromotionStatus;
    collective?: EventPromotionCollective;
}

export interface EventPromotionCollective extends BasePromotionCollective {
    self_managed?: boolean;
}
