import { IdName } from '@admin-clients/shared/data-access/models';

export interface ProductDeliveryPoint {
    product: IdName;
    event: IdName;
    delivery_point: IdName;
    is_default: boolean;
}
