import { PageableFilter } from '@admin-clients/shared/data-access/models';

export interface GetSaleRequestChannelSessionLinksRequest extends PageableFilter {
    saleRequestId?: number;
    language?: string;
    fields?: string[];
}
