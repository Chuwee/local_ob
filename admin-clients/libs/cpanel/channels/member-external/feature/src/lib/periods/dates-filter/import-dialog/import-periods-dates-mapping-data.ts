import { CsvHeaderMappingField } from '@admin-clients/shared/common/feature/csv';
import { PeriodsDates } from '@admin-clients/cpanel-channels-member-external-data-access';

export type CsvPeriodsDatesToImport = PeriodsDates;
export type CsvPeriodsDatesToImportValueTypes = CsvPeriodsDatesToImport[keyof CsvPeriodsDatesToImport];

export function createCsvPeriodsDatesToImportMappingFields():
    CsvHeaderMappingField<CsvPeriodsDatesToImport>[] {
    return [
        { key: 'user', header: 'MEMBER_EXTERNAL.LIMIT_PORTAL_ACCESS.IMPORT_DIALOG.USER', columnIndex: 1, required: true, example: '123456' },
        { key: 'date', header: 'MEMBER_EXTERNAL.LIMIT_PORTAL_ACCESS.IMPORT_DIALOG.DATE', columnIndex: 2, required: true, example: '2024-09-28' },
        { key: 'time', header: 'MEMBER_EXTERNAL.LIMIT_PORTAL_ACCESS.IMPORT_DIALOG.TIME', columnIndex: 3, required: true, example: '03:30' }
    ];
}
