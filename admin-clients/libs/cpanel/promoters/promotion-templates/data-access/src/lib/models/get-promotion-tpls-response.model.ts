import { ListResponse } from '@OneboxTM/utils-state';
import { PromotionType } from '@admin-clients/cpanel-common-promotions-utility-models';

export interface PromotionTplListElement {
    id: number;
    name: string;
    // TODO(MULTICURRENCY): mandatory when the migration to multicurrency is done
    currency_code?: string;
    favorite: boolean;
    type: PromotionType;
    presale: boolean;
    dates: {
        start: string;
        end: string;
    };
}

export interface GetPromotionTplsResponse extends ListResponse<PromotionTplListElement> {
}
