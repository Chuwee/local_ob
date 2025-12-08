import moment from 'moment';
import { VoucherCodesWithPinToImport, VoucherCodesToImport } from '@admin-clients/cpanel-vouchers-data-access';
import { CsvHeaderMappingField } from '@admin-clients/shared/common/feature/csv';

export type CsvVoucherCodesBasic = VoucherCodesToImport;
export type CsvVoucherCodesWithPin = VoucherCodesWithPinToImport;
export type CsvVoucherCodes = CsvVoucherCodesBasic & CsvVoucherCodesWithPin;
export type CsvVoucherCodesValueTypes = CsvVoucherCodes[keyof CsvVoucherCodes];

const dateExample = moment('07/05/2001', 'DD/MM/YYYY').format(moment.localeData().longDateFormat('L'));

export function createCsvVoucherCodesMappingFields(): CsvHeaderMappingField<CsvVoucherCodesBasic>[] {
    return [
        { key: 'balance', header: 'VOUCHER.EXPORT.BALANCE', columnIndex: null, required: true, example: '0' },
        { key: 'expiration', header: 'VOUCHER.EXPORT.EXPIRATION', columnIndex: null, example: dateExample },
        { key: 'email', header: 'VOUCHER.EXPORT.EMAIL', columnIndex: null, example: 'example@example.com' },
        { key: 'usage_limit', header: 'VOUCHER.EXPORT.USAGE_LIMIT', columnIndex: null, example: '1' }
    ];
}

export function createCsvVoucherCodesWithPinMappingFields(): CsvHeaderMappingField<CsvVoucherCodesWithPin>[] {
    return [
        { key: 'balance', header: 'VOUCHER.EXPORT.BALANCE', columnIndex: null, required: true, example: '0' },
        { key: 'expiration', header: 'VOUCHER.EXPORT.EXPIRATION', columnIndex: null, example: dateExample },
        { key: 'pin', header: 'VOUCHER.EXPORT.PIN', columnIndex: null, required: true, example: '1234' },
        { key: 'email', header: 'VOUCHER.EXPORT.EMAIL', columnIndex: null, example: 'example@example.com' },
        { key: 'usage_limit', header: 'VOUCHER.EXPORT.USAGE_LIMIT', columnIndex: null, example: '1' }
    ];
}
