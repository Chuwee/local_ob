import { CsvHeaderMappingField } from '@admin-clients/shared/common/feature/csv';
import { ListingToBlacklist } from '../../models/listings.model';

export type ListingValueTypes = ListingToBlacklist[keyof ListingToBlacklist];

export function createCsvBulkManageBlacklistListingsMappingFields(): CsvHeaderMappingField<ListingToBlacklist>[] {
    return [
        { key: 'code', header: 'LISTINGS.LISTINGS_LIST_EXPORT.CODE', columnIndex: null, required: true, example: 'G3LER5XNQ5D9868Y4K5A\r\n7D31Z7JQ3RDVAA2XE5Y9' }
    ];
}
