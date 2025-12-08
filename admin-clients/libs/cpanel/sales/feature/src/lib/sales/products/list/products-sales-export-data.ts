import { FieldDataGroup } from '@admin-clients/shared/data-access/models';

export const exportDataProduct: FieldDataGroup[] = [
    {
        fieldKey: 'TICKET.EXPORT.PRODUCT_DATA.TITLE',
        field: 'product',
        isDefault: false,
        fields: [
            {
                field: 'product.id',
                fieldKey: 'TICKET.EXPORT.PRODUCT_DATA.ID',
                isDefault: true
            },
            {
                field: 'product.name',
                fieldKey: 'TICKET.EXPORT.PRODUCT_DATA.NAME',
                isDefault: true
            },
            {
                field: 'product.variantId',
                fieldKey: 'TICKET.EXPORT.PRODUCT_DATA.VARIANT_ID',
                isDefault: true
            },
            {
                field: 'product.barcode',
                fieldKey: 'TICKET.EXPORT.PRODUCT_DATA.BARCODE',
                isDefault: true
            },
            {
                field: 'product.type',
                fieldKey: 'TICKET.EXPORT.PRODUCT_DATA.TYPE',
                isDefault: true
            },
            {
                field: 'product.producer.id',
                fieldKey: 'TICKET.EXPORT.PRODUCT_DATA.PRODUCER_ID',
                isDefault: false
            },
            {
                field: 'product.producer.name',
                fieldKey: 'TICKET.EXPORT.PRODUCT_DATA.PRODUCER_NAME',
                isDefault: false
            },
            {
                field: 'product.delivery.type',
                fieldKey: 'TICKET.EXPORT.PRODUCT_DATA.DELIVERY.TYPE',
                isDefault: false
            },
            {
                field: 'product.delivery.deliveryPointId',
                fieldKey: 'TICKET.EXPORT.PRODUCT_DATA.DELIVERY.DELIVERY_POINT_ID',
                isDefault: false
            },
            {
                field: 'product.delivery.deliveryPointName',
                fieldKey: 'TICKET.EXPORT.PRODUCT_DATA.DELIVERY.DELIVERY_POINT_NAME',
                isDefault: true
            },
            {
                field: 'product.delivery.deliveryFrom',
                fieldKey: 'TICKET.EXPORT.PRODUCT_DATA.DELIVERY.DELIVERY_FROM',
                isDefault: false
            },
            {
                field: 'product.delivery.deliveryTo',
                fieldKey: 'TICKET.EXPORT.PRODUCT_DATA.DELIVERY.DELIVERY_TO',
                isDefault: false
            },
            {
                field: 'product.delivery.session.name',
                fieldKey: 'TICKET.EXPORT.PRODUCT_DATA.DELIVERY.SESSION_NAME',
                isDefault: false
            },
            {
                field: 'product.delivery.session.id',
                fieldKey: 'TICKET.EXPORT.PRODUCT_DATA.DELIVERY.SESSION_ID',
                isDefault: false
            },
            {
                field: 'product.delivery.session.date',
                fieldKey: 'TICKET.EXPORT.PRODUCT_DATA.DELIVERY.SESSION_DATE',
                isDefault: false
            }
        ]
    },
    {
        fieldKey: 'TICKET.EXPORT.BUYER_DATA.TITLE',
        field: 'buyer_data',
        isDefault: false,
        fields: [
            {
                field: 'buyer_data.name',
                fieldKey: 'TICKET.EXPORT.BUYER_DATA.NAME',
                isDefault: true
            },
            {
                field: 'buyer_data.surname',
                fieldKey: 'TICKET.EXPORT.BUYER_DATA.SURNAME',
                isDefault: true
            },
            {
                field: 'buyer_data.phone',
                fieldKey: 'TICKET.EXPORT.BUYER_DATA.PHONE',
                isDefault: true
            },
            {
                field: 'buyer_data.email',
                fieldKey: 'TICKET.EXPORT.BUYER_DATA.EMAIL',
                isDefault: true
            },
            {
                field: 'buyer_data.address',
                fieldKey: 'TICKET.EXPORT.BUYER_DATA.ADDRESS',
                isDefault: false
            },
            {
                field: 'buyer_data.country',
                fieldKey: 'TICKET.EXPORT.BUYER_DATA.COUNTRY',
                isDefault: false
            },
            {
                field: 'buyer_data.gender',
                fieldKey: 'TICKET.EXPORT.BUYER_DATA.GENDER',
                isDefault: false
            },
            {
                field: 'buyer_data.language',
                fieldKey: 'TICKET.EXPORT.BUYER_DATA.LANGUAGE',
                isDefault: false
            },
            {
                field: 'buyer_data.birthday',
                fieldKey: 'TICKET.EXPORT.BUYER_DATA.BIRTHDAY',
                isDefault: false
            },
            {
                field: 'buyer_data.allow_commercial_mailing',
                fieldKey: 'TICKET.EXPORT.BUYER_DATA.ALLOW_COMMERCIAL_MAILING',
                isDefault: false
            },
            {
                field: 'buyer_data.subcountry',
                fieldKey: 'TICKET.EXPORT.BUYER_DATA.SUBCOUNTRY',
                isDefault: false
            },
            {
                field: 'buyer_data.zip_code',
                fieldKey: 'TICKET.EXPORT.BUYER_DATA.ZIP_CODE',
                isDefault: false
            },
            {
                field: 'buyer_data.city',
                fieldKey: 'TICKET.EXPORT.BUYER_DATA.CITY',
                isDefault: false
            },
            {
                field: 'buyer_data.id_number',
                fieldKey: 'TICKET.EXPORT.BUYER_DATA.ID_NUMBER',
                isDefault: false
            },
            {
                field: 'buyer_data.additional_info',
                fieldKey: 'TICKET.EXPORT.BUYER_DATA.ADDITIONAL_INFO',
                isDefault: false
            },
            {
                field: 'attendant',
                fieldKey: 'TICKET.EXPORT.BUYER_DATA.ATTENDANT',
                isDefault: false
            }
        ]
    },
    {
        fieldKey: 'TICKET.EXPORT.OPERATION_DATA.TITLE',
        field: 'operation_data',
        isDefault: false,
        fields: [
            {
                field: 'channel.entity.name',
                fieldKey: 'TICKET.EXPORT.OPERATION_DATA.ENTITY_NAME',
                isDefault: true
            },
            {
                field: 'channel.name',
                fieldKey: 'TICKET.EXPORT.OPERATION_DATA.CHANNEL_NAME',
                isDefault: true
            },
            {
                field: 'state',
                fieldKey: 'TICKET.EXPORT.OPERATION_DATA.STATE',
                isDefault: true
            },
            {
                field: 'order.code',
                fieldKey: 'TICKET.EXPORT.OPERATION_DATA.ORDER_CODE',
                isDefault: true
            },
            {
                field: 'order.date.date',
                fieldKey: 'TICKET.EXPORT.OPERATION_DATA.ORDER_DATE',
                isDefault: true
            },
            {
                field: 'order.date.time',
                fieldKey: 'TICKET.EXPORT.OPERATION_DATA.ORDER_DATE_TIME',
                isDefault: true
            },
            {
                field: 'order.previous_order.type',
                fieldKey: 'TICKET.EXPORT.OPERATION_DATA.PREVIOUS_ORDER_TYPE',
                isDefault: false
            },
            {
                field: 'order.previous_order.code',
                fieldKey: 'TICKET.EXPORT.OPERATION_DATA.PREVIOUS_ORDER_CODE',
                isDefault: false
            },
            {
                field: 'order.previous_order.date.date',
                fieldKey: 'TICKET.EXPORT.OPERATION_DATA.PREVIOUS_ORDER_DATE',
                isDefault: false
            },
            {
                field: 'order.previous_order.date.time',
                fieldKey: 'TICKET.EXPORT.OPERATION_DATA.PREVIOUS_ORDER_DATE_TIME',
                isDefault: false
            },
            {
                field: 'client_type',
                fieldKey: 'TICKET.EXPORT.OPERATION_DATA.CLIENT_TYPE',
                isDefault: true
            }
        ]
    },
    {
        fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.TITLE',
        field: 'payment_data',
        isDefault: false,
        fields: [
            {
                field: 'price.net',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.NET_TICKET',
                isDefault: false
            },
            {
                field: 'price.base',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.BASE_PRICE',
                isDefault: true
            },
            {
                field: 'price.pre_tax',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.PRE_TAX_ITEMS',
                isDefault: false
            },
            {
                field: 'price.tax',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.TAX',
                isDefault: false
            },
            {
                field: 'price.taxes.items',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.TAXES_ITEMS',
                isDefault: false
            },
            {
                field: 'price.charges.net.promoter',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.CHARGES_NET_PROMOTER',
                isDefault: false
            },
            {
                field: 'price.taxes.charges.promoter.value',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.CHARGES_PROMOTER_TAX_RATE',
                isDefault: false
            },
            {
                field: 'price.taxes.charges.promoter',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.TAXES_CHARGES',
                isDefault: false
            },
            {
                field: 'price.charges.promoter',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.CHARGES_PROMOTER',
                isDefault: true
            },
            {
                field: 'price.charges.channel',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.CHARGES_CHANNEL',
                isDefault: false
            },
            {
                field: 'payment_data.reference',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.REFERENCE',
                isDefault: true
            },
            {
                field: 'payment_data.merchant_code',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.MERCHANT_CODE',
                isDefault: false
            },
            {
                field: 'payment_data.payment_method',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.PAYMENT_METHOD',
                isDefault: false
            },
            {
                field: 'payment_data.gateway',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.GATEWAY',
                isDefault: false
            },
            {
                field: 'price.final',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.FINAL_PRICE',
                isDefault: false
            },
            {
                field: 'price.channel_commission',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.CHANNEL_COMMISSIONS',
                isDefault: false
            },
            {
                field: 'price.currency',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.CURRENCY',
                isDefault: false
            }
        ]
    }
];

