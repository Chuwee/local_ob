import { FieldDataGroup } from '@admin-clients/shared/data-access/models';
import { VoucherOrdersFields } from './voucher-orders-fields.enum';

export const voucherOrdersColumnList: FieldDataGroup[] = [
    {
        field: 'voucherOrders',
        fieldKey: 'VOUCHER-ORDERS',
        isDefault: false,
        fields: [

            {
                field: VoucherOrdersFields.code,
                fieldKey: 'FORMS.LABELS.CODE',
                isDefault: true,
                disabled: true
            },
            {
                field: VoucherOrdersFields.type,
                fieldKey: 'FORMS.LABELS.TYPE',
                isDefault: true
            },
            {
                field: VoucherOrdersFields.channel,
                fieldKey: 'FORMS.LABELS.CHANNEL',
                isDefault: true
            },
            {
                field: VoucherOrdersFields.purchaseDate,
                fieldKey: 'FORMS.LABELS.DATE',
                isDefault: true
            },
            {
                field: VoucherOrdersFields.client,
                fieldKey: 'FORMS.LABELS.CLIENT',
                isDefault: true
            },
            {
                field: VoucherOrdersFields.price,
                fieldKey: 'FORMS.LABELS.PRICE',
                isDefault: true,
                disabled: true
            }
        ]
    }
];
