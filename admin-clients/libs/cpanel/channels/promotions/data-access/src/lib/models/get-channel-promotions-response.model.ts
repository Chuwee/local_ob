import { ListResponse } from '@OneboxTM/utils-state';
import { PromotionStatus, PromotionValidityPeriodType } from '@admin-clients/cpanel-common-promotions-utility-models';
import { ChannelPromotionType } from './channel-promotion-type.enum';

export interface ChannelPromotionListElement {
    id: number;
    name: string;
    status: PromotionStatus;
    type: ChannelPromotionType;
    validity_period?: {
        type: PromotionValidityPeriodType;
        start_date?: string;
        end_date?: string;
    };
}

export interface GetChannelPromotionsResponse extends ListResponse<ChannelPromotionListElement> {
}
