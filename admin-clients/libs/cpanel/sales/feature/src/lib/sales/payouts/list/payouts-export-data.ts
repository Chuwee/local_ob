import { FieldDataGroup } from '@admin-clients/shared/data-access/models';

export const exportDataPayout: FieldDataGroup[] = [
    {
        fieldKey: 'PAYOUT.EXPORT.EVENT_DATA.TITLE',
        field: 'event_data',
        isDefault: true,
        fields: [
            {
                field: 'order_code',
                fieldKey: 'PAYOUT.EXPORT.ORDER_CODE',
                isDefault: true
            },
            {
                field: 'event',
                fieldKey: 'PAYOUT.EXPORT.EVENT',
                isDefault: true
            },
            {
                field: 'channel',
                fieldKey: 'PAYOUT.EXPORT.CHANNEL',
                isDefault: true
            },
            {
                field: 'session',
                fieldKey: 'PAYOUT.EXPORT.SESSION',
                isDefault: true
            },
            {
                field: 'seat',
                fieldKey: 'PAYOUT.EXPORT.SEAT',
                isDefault: true
            },
            {
                field: 'purchase_date',
                fieldKey: 'PAYOUT.EXPORT.PURCHASE_DATE',
                isDefault: true
            }
        ]
    }, {
        fieldKey: 'PAYOUT.EXPORT.PAYOUT_DATA.TITLE',
        field: 'payout_data',
        isDefault: true,
        fields: [
            {
                field: 'customer.name',
                fieldKey: 'PAYOUT.EXPORT.CUSTOMER',
                isDefault: true
            },
            {
                field: 'customer.iban',
                fieldKey: 'PAYOUT.EXPORT.IBAN',
                isDefault: true
            },
            {
                field: 'customer.bacs',
                fieldKey: 'PAYOUT.EXPORT.BACS',
                isDefault: false
            },
            {
                field: 'price',
                fieldKey: 'PAYOUT.EXPORT.AMOUNT',
                isDefault: true,
                disabled: true
            },
            {
                field: 'payout_status',
                fieldKey: 'PAYOUT.EXPORT.STATUS',
                isDefault: true,
                disabled: true
            }
        ]
    }
];

