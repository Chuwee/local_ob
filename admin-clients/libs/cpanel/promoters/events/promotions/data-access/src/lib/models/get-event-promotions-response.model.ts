import { ListResponse } from '@OneboxTM/utils-state';
import { PromotionTplListElement } from '@admin-clients/cpanel/promoters/promotion-templates/data-access';
import { PromotionStatus } from '@admin-clients/cpanel-common-promotions-utility-models';

export interface EventPromotionListElement extends PromotionTplListElement {
    status: PromotionStatus;
}

export interface GetEventPromotionsResponse extends ListResponse<EventPromotionListElement>{
}
