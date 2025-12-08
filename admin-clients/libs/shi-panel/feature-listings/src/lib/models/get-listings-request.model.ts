import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { SupplierName } from '@admin-clients/shi-panel/utility-models';
import { ListingStatus } from './listing-status.enum';

export interface GetListingsRequest extends PageableFilter {
    code?: string;
    supplier?: SupplierName[];
    status?: ListingStatus[];
    update_date_from?: string;
    update_date_to?: string;
    import_date_from?: string;
    import_date_to?: string;
}
