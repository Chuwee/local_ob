import { PageableFilter } from '@admin-clients/shared/data-access/models';

export interface GetSaleRequestPriceTypesRequest extends PageableFilter {
    saleRequestId?: string;
}
