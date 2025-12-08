
import { LiteralsToImport } from '@admin-clients/cpanel-channels-literals-data-access';
import { CsvHeaderMappingField } from '@admin-clients/shared/common/feature/csv';

export type CsvLiteralsToImport = LiteralsToImport;
export type CsvLiteralsToImportValueTypes = CsvLiteralsToImport[keyof CsvLiteralsToImport];

export function createCsvLiteralsToImportMappingFields(languages: string[]): CsvHeaderMappingField<LiteralsToImport>[] {
    return [
        { key: 'literalKey', header: 'LITERALS.EXTERNAL.EXPORT.KEY', columnIndex: null, required: true, example: 'KEY' },
        ...languages.map(lang =>
            ({ key: lang, header: lang, columnIndex: null, required: true, example: 'Some literal value' }))
    ];
}

