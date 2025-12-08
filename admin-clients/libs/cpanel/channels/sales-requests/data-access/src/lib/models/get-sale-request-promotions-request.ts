import { PageableFilter } from '@admin-clients/shared/data-access/models';

export interface GetSaleRequestPromotionsRequest extends PageableFilter {
    saleRequestId?: number;
}
