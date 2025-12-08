import { OrdersFields } from '@admin-clients/cpanel-sales-data-access';
import { FieldDataGroup } from '@admin-clients/shared/data-access/models';

export const ordersListColumn: FieldDataGroup[] = [
    {
        field: 'orders-list',
        fieldKey: 'ORDERS',
        isDefault: false,
        fields: [
            {
                field: OrdersFields.code,
                fieldKey: 'FORMS.LABELS.CODE',
                isDefault: true,
                disabled: true
            },
            {
                field: OrdersFields.event,
                fieldKey: 'FORMS.LABELS.EVENT',
                isDefault: true
            },
            {
                field: OrdersFields.channel,
                fieldKey: 'FORMS.LABELS.CHANNEL',
                isDefault: true
            },
            {
                field: OrdersFields.type,
                fieldKey: 'FORMS.LABELS.TYPE',
                isDefault: true
            },
            {
                field: OrdersFields.date,
                fieldKey: 'FORMS.LABELS.DATE',
                isDefault: true
            },
            {
                field: OrdersFields.client,
                fieldKey: 'FORMS.LABELS.CLIENT',
                isDefault: true
            },
            {
                field: OrdersFields.ticketsCount,
                fieldKey: 'ORDER.TICKETS',
                isDefault: true
            },
            {
                field: OrdersFields.productsCount,
                fieldKey: 'FORMS.LABELS.PRODUCTS',
                isDefault: true
            },
            {
                field: OrdersFields.basePrice,
                fieldKey: 'FORMS.LABELS.BASE_PRICE',
                isDefault: true
            },
            {
                field: OrdersFields.promotions,
                fieldKey: 'ORDER.PROMOTIONS',
                isDefault: true
            },
            {
                field: OrdersFields.charges,
                fieldKey: 'ORDERS.CHARGES',
                isDefault: true
            },
            {
                field: OrdersFields.donation,
                fieldKey: 'ORDERS.DONATION',
                isDefault: false
            },
            {
                field: OrdersFields.delivery,
                fieldKey: 'ORDER.DELIVERY_METHOD',
                isDefault: false
            },
            {
                field: OrdersFields.priceGateway,
                fieldKey: 'ORDER.GATEWAY_PRICE',
                isDefault: false
            },
            {
                field: OrdersFields.internationalPhonePrefix,
                fieldKey: 'ORDER.INTERNATIONAL_PHONE.PREFIX',
                isDefault: false
            },
            {
                field: OrdersFields.internationalPhoneNumber,
                fieldKey: 'ORDER.INTERNATIONAL_PHONE.NUMBER',
                isDefault: false
            },
            {
                field: OrdersFields.reallocationRefund,
                fieldKey: 'ORDER.REALLOCATION_REFUND',
                isDefault: false
            },
            {
                field: OrdersFields.finalPrice,
                fieldKey: 'ORDER.TOTAL_PRICE',
                isDefault: true,
                disabled: true
            }
        ]
    }
];
