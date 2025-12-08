import { CsvHeaderMappingField } from '@admin-clients/shared/common/feature/csv';
import { MappingToCreate } from '../../models/mapping.model';

export type MappingValueTypes = MappingToCreate[keyof MappingToCreate];

export function createCsvBulkCreateMappingsFields(): CsvHeaderMappingField<MappingToCreate>[] {
    return [
        { key: 'shi_id', header: 'MAPPINGS.ID', columnIndex: null, required: true, example: '1009092' },
        { key: 'supplier_id', header: 'MAPPINGS.SUPPLIER_ID', columnIndex: null, required: true, example: '1111111' },
        { key: 'supplier', header: 'MAPPINGS.SUPPLIER', columnIndex: null, example: 'LOGITIX' },
        { key: 'favorite', header: 'MAPPINGS.FAVORITE', columnIndex: null, example: 'false' }
    ];
}
