import { CsvHeaderMappingField } from '@admin-clients/shared/common/feature/csv';
import { MappingToClean } from '../../models/mapping.model';

export type MappingValueTypes = MappingToClean[keyof MappingToClean];

export function createCsvEventListingsMappingFields(): CsvHeaderMappingField<MappingToClean>[] {
    return [{
        key: 'shi_id', header: 'MAPPINGS.MAPPINGS_LIST_EXPORT.SHI_ID', columnIndex: null, required: true, example: 1009092 + '\r\n' + 1009091
    }];
}
