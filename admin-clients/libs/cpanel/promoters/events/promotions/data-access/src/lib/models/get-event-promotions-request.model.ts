import { GetPromotionTplsRequest } from '@admin-clients/cpanel/promoters/promotion-templates/data-access';
import { PromotionStatus } from '@admin-clients/cpanel-common-promotions-utility-models';

export interface GetEventPromotionsRequest extends GetPromotionTplsRequest {
    status?: PromotionStatus;
}
