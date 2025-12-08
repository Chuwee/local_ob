import { Id, IdName } from '@admin-clients/shared/data-access/models';

export enum SaleRequestSurchargeTaxesOrigin {
    event = 'EVENT',
    channel = 'CHANNEL',
    saleRequest = 'SALE_REQUEST'
}

export interface SaleRequestSurchargeTaxes {
    origin: SaleRequestSurchargeTaxesOrigin;
    taxes?: IdName[];
}

export interface PutSaleRequestSurchargeTaxes {
    origin: SaleRequestSurchargeTaxesOrigin;
    taxes?: Id[];
}
