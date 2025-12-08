import { ProductDeliveryType } from './product-delivery-type.model';
import { ProductDeliveryUnits } from './product-delivery-units.model';

export interface ProductDeliveryConfig {
    delivery_type: ProductDeliveryType;
    start_time_unit: ProductDeliveryUnits;
    start_time_value: number;
    end_time_unit: ProductDeliveryUnits;
    end_time_value: number;
    delivery_date_from: string;
    delivery_date_to: string;
}
