import { FieldData, FieldDataGroup } from '@admin-clients/shared/data-access/models';

export const exportDataVoucher: FieldDataGroup[] = [
    {
        fieldKey: 'VOUCHER.EXPORT.TITLE',
        field: 'voucher_data',
        isDefault: true,
        fields: [
            {
                field: 'code',
                fieldKey: 'VOUCHER.EXPORT.CODE',
                isDefault: true
            },
            {
                field: 'status',
                fieldKey: 'VOUCHER.EXPORT.STATUS',
                isDefault: true
            },
            {
                field: 'email',
                fieldKey: 'VOUCHER.EXPORT.EMAIL',
                isDefault: true
            },
            {
                field: 'balance',
                fieldKey: 'VOUCHER.EXPORT.BALANCE',
                isDefault: true
            },
            {
                field: 'expiration',
                fieldKey: 'VOUCHER.EXPORT.EXPIRATION',
                isDefault: true
            },
            {
                field: 'usages.used',
                fieldKey: 'VOUCHER.EXPORT.USAGES',
                isDefault: true
            },
            {
                field: 'usages.limit',
                fieldKey: 'VOUCHER.EXPORT.USAGE_LIMIT',
                isDefault: true
            }
        ]
    }
];

export const pinExportField: FieldData = {
    field: 'pin',
    fieldKey: 'VOUCHER.EXPORT.PIN',
    isDefault: true
};

