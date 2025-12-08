import { ExternalBarcode, ExternalBarcodeAttendantData } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { CsvHeaderMappingField } from '@admin-clients/shared/common/feature/csv';

export type CsvBarcode = ExternalBarcode & Partial<ExternalBarcodeAttendantData>;
export type CsvBarcodeValueTypes = CsvBarcode[keyof CsvBarcode];

export function createCsvBarcodeMappingFields(): CsvHeaderMappingField<CsvBarcode>[] {
    return [
        { key: 'barcode', header: 'EVENTS.SESSION.WHITE_LIST.IMPORT.BARCODE', columnIndex: null, required: true },
        { key: 'locator', header: 'EVENTS.SESSION.WHITE_LIST.IMPORT.LOCATOR', columnIndex: null },
        { key: 'row', header: 'EVENTS.SESSION.WHITE_LIST.IMPORT.ROW', columnIndex: null },
        { key: 'seat', header: 'EVENTS.SESSION.WHITE_LIST.IMPORT.SEAT', columnIndex: null },
        { key: 'accessId', header: 'EVENTS.SESSION.WHITE_LIST.IMPORT.ACCESS_ID', columnIndex: null },
        { key: 'ATTENDANT_NAME', header: 'EVENTS.SESSION.WHITE_LIST.IMPORT.ATTENDANT_NAME', columnIndex: null },
        { key: 'ATTENDANT_SURNAME', header: 'EVENTS.SESSION.WHITE_LIST.IMPORT.ATTENDANT_SURNAME', columnIndex: null },
        { key: 'ATTENDANT_ID_NUMBER', header: 'EVENTS.SESSION.WHITE_LIST.IMPORT.ATTENDANT_ID_NUMBER', columnIndex: null },
        { key: 'ATTENDANT_MAIL', header: 'EVENTS.SESSION.WHITE_LIST.IMPORT.ATTENDANT_MAIL', columnIndex: null }
    ];
}
