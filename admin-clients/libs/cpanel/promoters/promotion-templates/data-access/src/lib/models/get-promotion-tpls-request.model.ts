import { PromotionType } from '@admin-clients/cpanel-common-promotions-utility-models';
import { PageableFilter } from '@admin-clients/shared/data-access/models';

export interface GetPromotionTplsRequest extends PageableFilter {
    entityId?: number;
    type?: PromotionType;
}
