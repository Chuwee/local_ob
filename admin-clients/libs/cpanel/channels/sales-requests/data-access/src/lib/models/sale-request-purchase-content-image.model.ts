import { ContentImage } from '@admin-clients/shared/data-access/models';
import { SaleRequestPurchaseContentImageType } from './sale-request-purchase-content-image-type.enum';

export interface SaleRequestPurchaseContentImage extends ContentImage<SaleRequestPurchaseContentImageType> {
    image_url?: string;
}

export interface SaleRequestPurchaseContentImageField {
    formField: string;
    type: SaleRequestPurchaseContentImageType;
}
