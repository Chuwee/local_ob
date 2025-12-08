import {
    CollectiveCodesToImport,
    CollectiveCodesUserPassToImport,
    CollectiveCodesUserToImport
} from '@admin-clients/cpanel/collectives/data-access';
import { CsvHeaderMappingField } from '@admin-clients/shared/common/feature/csv';
import moment from 'moment';

export type CsvCollectiveCodesBasic = CollectiveCodesToImport;
export type CsvCollectiveCodesUser = CollectiveCodesUserToImport;
export type CsvCollectiveCodesUserPass = CollectiveCodesUserPassToImport;
export type CsvCollectiveCodes = CsvCollectiveCodesBasic & CsvCollectiveCodesUser & CsvCollectiveCodesUserPass;
export type CsvCollectiveCodesValueTypes = CsvCollectiveCodes[keyof CsvCollectiveCodes];

const dateExample1 = moment('10/05/2001', 'DD/MM/YYYY').format(moment.localeData().longDateFormat('L'));
const dateExample2 = moment('10/05/2025', 'DD/MM/YYYY').format(moment.localeData().longDateFormat('L'));

export function createCsvCollectiveCodesMappingFields(): CsvHeaderMappingField<CsvCollectiveCodesBasic>[] {
    return [
        { key: 'code', header: 'COLLECTIVE.CODES.EXPORT.CODE', columnIndex: null, required: true, example: 'CODIGO123' },
        { key: 'usage_limit', header: 'COLLECTIVE.CODES.EXPORT.USAGE_LIMIT', columnIndex: null, example: '0' },
        { key: 'validity_from', header: 'COLLECTIVE.CODES.EXPORT.VALIDITY_FROM', columnIndex: null, example: dateExample1 },
        { key: 'validity_to', header: 'COLLECTIVE.CODES.EXPORT.VALIDITY_TO', columnIndex: null, example: dateExample2 }
    ];
}

export function createCsvCollectiveCodesUserMappingFields(): CsvHeaderMappingField<CsvCollectiveCodesUser>[] {
    return [
        { key: 'user', header: 'COLLECTIVE.CODES.EXPORT.USER', columnIndex: null, required: true, example: 'USER123' },
        { key: 'usage_limit', header: 'COLLECTIVE.CODES.EXPORT.USAGE_LIMIT', columnIndex: null, example: '0' },
        { key: 'validity_from', header: 'COLLECTIVE.CODES.EXPORT.VALIDITY_FROM', columnIndex: null, example: dateExample1 },
        { key: 'validity_to', header: 'COLLECTIVE.CODES.EXPORT.VALIDITY_TO', columnIndex: null, example: dateExample2 }
    ];
}

export function createCsvCollectiveCodesUserPassMappingFields(): CsvHeaderMappingField<CsvCollectiveCodesUserPass>[] {
    return [
        { key: 'user', header: 'COLLECTIVE.CODES.EXPORT.USER', columnIndex: null, required: true, example: 'USER123' },
        { key: 'key', header: 'COLLECTIVE.CODES.EXPORT.KEY', columnIndex: null, required: true, example: 'PASS123' },
        { key: 'usage_limit', header: 'COLLECTIVE.CODES.EXPORT.USAGE_LIMIT', columnIndex: null, example: '0' },
        { key: 'validity_from', header: 'COLLECTIVE.CODES.EXPORT.VALIDITY_FROM', columnIndex: null, example: dateExample1 },
        { key: 'validity_to', header: 'COLLECTIVE.CODES.EXPORT.VALIDITY_TO', columnIndex: null, example: dateExample2 }
    ];
}
