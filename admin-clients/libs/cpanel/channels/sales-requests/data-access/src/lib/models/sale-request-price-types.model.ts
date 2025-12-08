import { ListResponse } from '@OneboxTM/utils-state';

export interface SaleRequestPriceTypesResponse extends ListResponse<SaleRequestPriceTypes> {
}

export interface SaleRequestPriceTypes {
    id: number;
    name: string;
    venue_template: {
        id: number;
        name: string;
    };
}
