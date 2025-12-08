import { FieldDataGroup } from '@admin-clients/shared/data-access/models';

export const exportDataVoucherOrder: FieldDataGroup[] = [
    {
        fieldKey: 'VOUCHER_ORDER.EXPORT.OPERATION_DATA.TITLE',
        field: 'operation_data',
        isDefault: true,
        fields: [
            {
                field: 'type',
                fieldKey: 'VOUCHER_ORDER.EXPORT.OPERATION_DATA.TYPE',
                isDefault: true
            },
            {
                field: 'code',
                fieldKey: 'VOUCHER_ORDER.EXPORT.OPERATION_DATA.CODE',
                isDefault: true
            },
            {
                field: 'state',
                fieldKey: 'VOUCHER_ORDER.EXPORT.OPERATION_DATA.STATE',
                isDefault: true
            },
            {
                field: 'channel_id',
                fieldKey: 'VOUCHER_ORDER.EXPORT.OPERATION_DATA.CHANNEL_ID',
                isDefault: true
            },
            {
                field: 'channel_name',
                fieldKey: 'VOUCHER_ORDER.EXPORT.OPERATION_DATA.CHANNEL_NAME',
                isDefault: true
            },
            {
                field: 'entity_id',
                fieldKey: 'VOUCHER_ORDER.EXPORT.OPERATION_DATA.CHANNEL_ENTITY_ID',
                isDefault: true
            },
            {
                field: 'operator_id',
                fieldKey: 'VOUCHER_ORDER.EXPORT.OPERATION_DATA.OPERATOR_ID',
                isDefault: true
            },
            {
                field: 'language',
                fieldKey: 'VOUCHER_ORDER.EXPORT.OPERATION_DATA.LANGUAGE',
                isDefault: true
            },
            {
                field: 'currency',
                fieldKey: 'VOUCHER_ORDER.EXPORT.OPERATION_DATA.CURRENCY',
                isDefault: true
            },
            {
                field: 'final_price',
                fieldKey: 'VOUCHER_ORDER.EXPORT.OPERATION_DATA.FINAL_PRICE',
                isDefault: true
            },
            {
                field: 'purchase_date',
                fieldKey: 'VOUCHER_ORDER.EXPORT.OPERATION_DATA.PURCHASE_DATE',
                isDefault: true
            },
            {
                field: 'expiration_date',
                fieldKey: 'VOUCHER_ORDER.EXPORT.OPERATION_DATA.EXPIRATION_DATE',
                isDefault: true
            },
            {
                field: 'items.email_image',
                fieldKey: 'VOUCHER_ORDER.EXPORT.OPERATION_DATA.EMAIL_IMAGE',
                isDefault: true
            },
            {
                field: 'items.email_delivery_status',
                fieldKey: 'VOUCHER_ORDER.EXPORT.OPERATION_DATA.EMAIL_DELIVERY_STATUS',
                isDefault: true
            },
            {
                field: 'items.email_delivery_date',
                fieldKey: 'VOUCHER_ORDER.EXPORT.OPERATION_DATA.EMAIL_DELIVERY_DATE',
                isDefault: true
            },
            {
                field: 'items.receiver_data.name',
                fieldKey: 'VOUCHER_ORDER.EXPORT.OPERATION_DATA.RECEIVER.NAME',
                isDefault: true
            },
            {
                field: 'items.receiver_data.lastname',
                fieldKey: 'VOUCHER_ORDER.EXPORT.OPERATION_DATA.RECEIVER.LAST_NAME',
                isDefault: true
            },
            {
                field: 'items.receiver_data.email',
                fieldKey: 'VOUCHER_ORDER.EXPORT.OPERATION_DATA.RECEIVER.EMAIL',
                isDefault: true
            },
            {
                field: 'items.receiver_data.email_message',
                fieldKey: 'VOUCHER_ORDER.EXPORT.OPERATION_DATA.RECEIVER.EMAIL_MESSAGE',
                isDefault: true
            },
            {
                field: 'items.receiver_data.email_scheduled_date',
                fieldKey: 'VOUCHER_ORDER.EXPORT.OPERATION_DATA.RECEIVER.EMAIL_SCHEDULED_DATE',
                isDefault: true
            }
        ]
    }, {
        fieldKey: 'VOUCHER_ORDER.EXPORT.BUYER_DATA.TITLE',
        field: 'buyer_data',
        isDefault: false,
        fields: [
            {
                field: 'buyer_data.name',
                fieldKey: 'VOUCHER_ORDER.EXPORT.BUYER_DATA.NAME',
                isDefault: true
            },
            {
                field: 'buyer_data.lastname',
                fieldKey: 'VOUCHER_ORDER.EXPORT.BUYER_DATA.LAST_NAME',
                isDefault: true
            },
            {
                field: 'buyer_data.email',
                fieldKey: 'VOUCHER_ORDER.EXPORT.BUYER_DATA.EMAIL',
                isDefault: true
            }
        ]
    },
    {
        fieldKey: 'VOUCHER_ORDER.EXPORT.VOUCHER_DATA.TITLE',
        field: 'voucher_data',
        isDefault: false,
        fields: [
            {
                field: 'items.voucher_group_id',
                fieldKey: 'VOUCHER_ORDER.EXPORT.VOUCHER_DATA.GROUP_ID',
                isDefault: true
            },
            {
                field: 'items.voucher_group_name',
                fieldKey: 'VOUCHER_ORDER.EXPORT.VOUCHER_DATA.GROUP_NAME',
                isDefault: true
            },
            {
                field: 'items.voucher_code',
                fieldKey: 'VOUCHER_ORDER.EXPORT.VOUCHER_DATA.CODE',
                isDefault: true
            },
            {
                field: 'items.balance',
                fieldKey: 'VOUCHER_ORDER.EXPORT.VOUCHER_DATA.BALANCE',
                isDefault: true
            }
        ]
    },
    {
        fieldKey: 'VOUCHER_ORDER.EXPORT.PAYMENT_DATA.TITLE',
        field: 'payments',
        isDefault: false,
        fields: [
            {
                field: 'payments.value.cash',
                fieldKey: 'VOUCHER_ORDER.EXPORT.PAYMENT_DATA.VALUE_CASH',
                isDefault: true
            },
            {
                field: 'payments.value.credit_card',
                fieldKey: 'VOUCHER_ORDER.EXPORT.PAYMENT_DATA.VALUE_CREDIT_CARD',
                isDefault: true
            },
            {
                field: 'payments.value.bizum',
                fieldKey: 'VOUCHER_ORDER.EXPORT.PAYMENT_DATA.VALUE_BIZUM',
                isDefault: true
            },
            {
                field: 'payments.value.other',
                fieldKey: 'VOUCHER_ORDER.EXPORT.PAYMENT_DATA.VALUE_OTHER',
                isDefault: true
            },
            {
                field: 'payments.payment_reference',
                fieldKey: 'VOUCHER_ORDER.EXPORT.PAYMENT_DATA.REFERENCE',
                isDefault: true
            },
            {
                field: 'payments.payment_merchant',
                fieldKey: 'VOUCHER_ORDER.EXPORT.PAYMENT_DATA.MERCHANT',
                isDefault: true
            },
            {
                field: 'payments.gateways',
                fieldKey: 'VOUCHER_ORDER.EXPORT.PAYMENT_DATA.GATEWAYS',
                isDefault: true
            }
        ]
    }
];

