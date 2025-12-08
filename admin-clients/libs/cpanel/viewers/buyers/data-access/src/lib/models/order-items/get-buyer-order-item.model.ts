import { PageableFilter } from '@admin-clients/shared/data-access/models';

export interface GetBuyerOrderItemsRequest extends PageableFilter {
    product_type?: BuyerOrderItemType;
}

export enum BuyerOrderItemType {
    seat = 'SEAT',
    product = 'PRODUCT'
}
