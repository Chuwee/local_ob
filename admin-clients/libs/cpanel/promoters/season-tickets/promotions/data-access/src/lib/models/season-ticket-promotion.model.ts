import { ListResponse } from '@OneboxTM/utils-state';
import {
    BasePromotion,
    BasePromotionCollective, PostBasePromotion,
    PromotionPriceTypesScope, PromotionRatesScope
} from '@admin-clients/cpanel/promoters/data-access';
import { PromotionStatus, PromotionType } from '@admin-clients/cpanel-common-promotions-utility-models';
import { PageableFilter } from '@admin-clients/shared/data-access/models';

export interface SeasonTicketPromotion extends BasePromotion {
    status?: PromotionStatus;
    collective?: SeasonTicketPromotionCollective;
}

export interface SeasonTicketPromotionCollective extends BasePromotionCollective {
    self_managed?: boolean;
}

export interface GetSeasonTicketPromotionsRequest extends PageableFilter {
    status?: PromotionStatus;
    entityId?: number;
    type?: PromotionType;
}
export interface SeasonTicketPromotionListElement {
    status: PromotionStatus;
    id: number;
    name: string;
    favorite: boolean;
    type: PromotionType;
    dates: {
        start: string;
        end: string;
    };
}

export interface GetSeasonTicketPromotionsResponse extends ListResponse<SeasonTicketPromotionListElement> {
}

export interface PostSeasonTicketPromotion extends PostBasePromotion {
    from_entity_template_id?: number;
}

export interface SeasonTicketPromotionPriceTypes {
    type: PromotionPriceTypesScope;
    price_types: {
        id: number;
        name: string;
    }[];
}

export interface PutSeasonTicketPromotionPriceTypes {
    type: PromotionPriceTypesScope;
    price_types: number[];
}

export interface SeasonTicketPromotionRates {
    type: PromotionRatesScope;
    rates: {
        id: number;
        name: string;
    }[];
}

export interface PutSeasonTicketPromotionRates {
    type: PromotionRatesScope;
    rates: number[];
}

