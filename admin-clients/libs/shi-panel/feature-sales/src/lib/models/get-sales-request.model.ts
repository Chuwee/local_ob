import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { SupplierName } from '@admin-clients/shi-panel/utility-models';
import { DeliveryMethod } from './delivery-method.enum';
import { SaleStatus } from './sale-status.enum';

export interface GetSalesRequest extends PageableFilter {
    code?: string;
    listing_id?: number;
    event_id?: number;
    supplier?: SupplierName[];
    status?: SaleStatus[];
    delivery_method?: DeliveryMethod[];
    sale_date_from?: string;
    sale_date_to?: string;
    update_date_from?: string;
    update_date_to?: string;
    inhand_date_from?: string;
    inhand_date_to?: string;
    country_code?: string[];
    currency?: string[];
    taxonomies?: string[];
    daysToEventLte?: number;
    daysToEventGte?: number;
    last_error_description?: string[];
}
