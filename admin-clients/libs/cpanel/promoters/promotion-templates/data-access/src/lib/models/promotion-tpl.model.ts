import { BasePromotion } from '@admin-clients/cpanel/promoters/data-access';
import { IdName } from '@admin-clients/shared/data-access/models';

export interface PromotionTpl extends BasePromotion {
    entity?: IdName;
    favorite?: boolean;
}
