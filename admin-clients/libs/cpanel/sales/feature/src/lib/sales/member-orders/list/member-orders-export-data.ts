import { FieldDataGroup } from '@admin-clients/shared/data-access/models';

export const exportDataMemberOrder: FieldDataGroup[] = [
    {
        fieldKey: 'MEMBER_ORDER.EXPORT.OPERATION_DATA.TITLE',
        field: 'operation_data',
        isDefault: true,
        fields: [
            {
                field: 'type',
                fieldKey: 'MEMBER_ORDER.EXPORT.OPERATION_DATA.TYPE',
                isDefault: true
            },
            {
                field: 'code',
                fieldKey: 'MEMBER_ORDER.EXPORT.OPERATION_DATA.CODE',
                isDefault: true
            },
            {
                field: 'state',
                fieldKey: 'MEMBER_ORDER.EXPORT.OPERATION_DATA.STATE',
                isDefault: true
            },
            {
                field: 'date.date',
                fieldKey: 'MEMBER_ORDER.EXPORT.OPERATION_DATA.DATE',
                isDefault: true
            },
            {
                field: 'date.time',
                fieldKey: 'MEMBER_ORDER.EXPORT.OPERATION_DATA.TIME',
                isDefault: true
            },
            {
                field: 'club',
                fieldKey: 'MEMBER_ORDER.EXPORT.OPERATION_DATA.CLUB',
                isDefault: false
            },
            {
                field: 'channel.entity.name',
                fieldKey: 'MEMBER_ORDER.EXPORT.OPERATION_DATA.CHANNEL_ENTITY_NAME',
                isDefault: false
            },
            {
                field: 'channel.name',
                fieldKey: 'MEMBER_ORDER.EXPORT.OPERATION_DATA.CHANNEL_NAME',
                isDefault: false
            },
            {
                field: 'language',
                fieldKey: 'MEMBER_ORDER.EXPORT.OPERATION_DATA.LANG',
                isDefault: false
            },
            {
                field: 'agreements.commercial_mailing_agreement',
                fieldKey: 'MEMBER_ORDER.EXPORT.OPERATION_DATA.COMMERCIAL_MAILING_AGREEMENT',
                isDefault: true
            },
            {
                field: 'agreements.channel_agreements',
                fieldKey: 'MEMBER_ORDER.EXPORT.OPERATION_DATA.CHANNEL_AGREEMENTS',
                isDefault: true
            }
        ]
    }, {
        fieldKey: 'MEMBER_ORDER.EXPORT.BUYER_DATA.TITLE',
        field: 'buyer_data',
        isDefault: false,
        fields: [
            {
                field: 'buyer_data.name',
                fieldKey: 'MEMBER_ORDER.EXPORT.BUYER_DATA.NAME',
                isDefault: true
            },
            {
                field: 'buyer_data.surname',
                fieldKey: 'MEMBER_ORDER.EXPORT.BUYER_DATA.SURNAME',
                isDefault: true
            },
            {
                field: 'buyer_data.email',
                fieldKey: 'MEMBER_ORDER.EXPORT.BUYER_DATA.EMAIL',
                isDefault: true
            },
            {
                field: 'buyer_data.person_id',
                fieldKey: 'MEMBER_ORDER.EXPORT.BUYER_DATA.PERSON_ID',
                isDefault: true
            },
            {
                field: 'buyer_data.partner_id',
                fieldKey: 'MEMBER_ORDER.EXPORT.BUYER_DATA.PARTNER_ID',
                isDefault: true
            }
        ]
    },
    {
        fieldKey: 'MEMBER_ORDER.EXPORT.PAYMENT_DATA.TITLE',
        field: 'payment_data',
        isDefault: false,
        fields: [
            {
                field: 'price.base_price',
                fieldKey: 'MEMBER_ORDER.EXPORT.PAYMENT_DATA.BASE_PRICE',
                isDefault: true
            },
            {
                field: 'price.discounts',
                fieldKey: 'MEMBER_ORDER.EXPORT.PAYMENT_DATA.DISCOUNTS',
                isDefault: true
            },
            {
                field: 'price.charges',
                fieldKey: 'MEMBER_ORDER.EXPORT.PAYMENT_DATA.CHARGES',
                isDefault: true
            },
            {
                field: 'price.final_price',
                fieldKey: 'MEMBER_ORDER.EXPORT.PAYMENT_DATA.FINAL_PRICE',
                isDefault: true
            },
            {
                field: 'payment_type',
                fieldKey: 'MEMBER_ORDER.EXPORT.PAYMENT_DATA.TYPE',
                isDefault: true
            },
            {
                field: 'gateway_sid',
                fieldKey: 'MEMBER_ORDER.EXPORT.PAYMENT_DATA.GATEWAY',
                isDefault: true
            },
            {
                field: 'conf_sid',
                fieldKey: 'MEMBER_ORDER.EXPORT.PAYMENT_DATA.CONF_SID',
                isDefault: false
            },
            {
                field: 'payment_reference',
                fieldKey: 'MEMBER_ORDER.EXPORT.PAYMENT_DATA.REFERENCE',
                isDefault: true
            }
        ]
    }
];

