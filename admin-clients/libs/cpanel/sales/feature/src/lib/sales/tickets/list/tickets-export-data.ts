import { FieldDataGroup } from '@admin-clients/shared/data-access/models';

export const exportDataTicket: FieldDataGroup[] = [
    {
        fieldKey: 'TICKET.EXPORT.TICKET_DATA.TITLE',
        field: 'ticket_data',
        isDefault: false,
        fields: [
            {
                field: 'ticket.allocation.event.entity.name',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.EVENT_ENTITY_NAME',
                isDefault: true
            },
            {
                field: 'ticket.allocation.event.promoter.name',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.EVENT_PROMOTER_NAME',
                isDefault: true
            },
            {
                field: 'ticket.allocation.event.tour',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.TOUR',
                isDefault: false
            },
            {
                field: 'ticket.allocation.event.name',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.EVENT_NAME',
                isDefault: true
            },
            {
                field: 'ticket.allocation.event.reference',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.EVENT_REFERENCE',
                isDefault: false
            },
            {
                field: 'ticket.allocation.venue.name',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.VENUE_NAME',
                isDefault: true
            },
            {
                field: 'ticket.allocation.session.name',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.SESSION_NAME',
                isDefault: true
            },
            {
                field: 'ticket.allocation.session.date.start',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.START_DATE',
                isDefault: true
            },
            {
                field: 'ticket.allocation.sector.name',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.SECTOR_NAME',
                isDefault: true
            },
            {
                field: 'ticket.allocation.access.name',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.ACCESS_NAME',
                isDefault: false
            },
            {
                field: 'ticket.allocation.row.name',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.ROW_NAME',
                isDefault: true
            },
            {
                field: 'ticket.allocation.seat.name',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.SEAT_NAME',
                isDefault: true
            },
            {
                field: 'ticket.allocation.not_numbered_area.name',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.NOT_NUMBERED_AREA_NAME',
                isDefault: true
            },
            {
                field: 'ticket.tier_id',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.TIER_ID',
                isDefault: false
            },
            {
                field: 'ticket.tier_name',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.TIER_NAME',
                isDefault: false
            },
            {
                field: 'ticket.allocation.price_type.name',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.PRICETYPE_NAME',
                isDefault: true
            },
            {
                field: 'ticket.allocation.event.category.description',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.CATEGORY_DESCRIPTION',
                isDefault: false
            },
            {
                field: 'ticket.allocation.event.category.custom.description',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.CUSTOM_CATEGORY_DESCRIPTION',
                isDefault: false
            },
            {
                field: 'ticket.total_prints',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.PRINTS',
                isDefault: true
            },
            {
                field: 'ticket.validation.totals',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.VALIDATIONS',
                isDefault: true
            },
            {
                field: 'ticket.barcode',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.BARCODE',
                isDefault: true
            },
            {
                field: 'ticket.rate',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.RATE',
                isDefault: false
            },
            {
                field: 'ticket.group',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.GROUP',
                isDefault: false
            },
            {
                field: 'order.external_code',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.EXTERNAL_CODE',
                isDefault: true
            },
            {
                field: 'ticket.external_properties.access',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.EXTERNAL_ACCESS',
                isDefault: true
            },
            {
                field: 'ticket.external_properties.reference',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.EXTERNAL_REFERENCE',
                isDefault: true
            },
            {
                field: 'ticket.external_properties.gate',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.EXTERNAL_GATE',
                isDefault: true
            },
            {
                field: 'ticket.external_properties.zone',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.EXTERNAL_ZONE',
                isDefault: true
            },
            {
                field: 'ticket.type',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.TYPE',
                isDefault: true
            },
            {
                field: 'origin_market',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.ORIGIN_MARKET',
                isDefault: false
            },
            {
                field: 'ticket.from_renewal',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.FROM_RENEWAL',
                isDefault: false
            }
        ]
    },
    {
        fieldKey: 'TICKET.EXPORT.CHANGE_SEAT_DATA.TITLE',
        field: 'change_seat_data',
        isDefault: false,
        fields: [
            {
                field: 'ticket.reallocation_refund',
                fieldKey: 'TICKET.EXPORT.CHANGE_SEAT_DATA.REALLOCATION_REFUND',
                isDefault: false
            },
            {
                field: 'price.charges.net.ticket_reallocation',
                fieldKey: 'TICKET.EXPORT.CHANGE_SEAT_DATA.NET_SURCHARGE',
                isDefault: false
            },
            {
                field: 'price.taxes.charges.ticket_reallocation',
                fieldKey: 'TICKET.EXPORT.CHANGE_SEAT_DATA.TAX_SURCHARGE',
                isDefault: false
            },
            {
                field: 'price.charges.ticket_reallocation',
                fieldKey: 'TICKET.EXPORT.CHANGE_SEAT_DATA.CHARGES.REALLOCATION',
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
                field: 'ticket.group_data.name',
                fieldKey: 'TICKET.EXPORT.BUYER_DATA.GROUP_NAME',
                isDefault: false
            },
            {
                field: 'ticket.group_data.attendees',
                fieldKey: 'TICKET.EXPORT.BUYER_DATA.ATTENDEES',
                isDefault: false
            },
            {
                field: 'ticket.group_data.accompanists',
                fieldKey: 'TICKET.EXPORT.BUYER_DATA.ACCOMPANISTS',
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
        fieldKey: 'TICKET.EXPORT.B2B_DATA.TITLE',
        field: 'b2b_data',
        isDefault: false,
        fields: [
            {
                field: 'buyer_data.b2b_data.name',
                fieldKey: 'TICKET.EXPORT.B2B_DATA.B2B_NAME',
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
                field: 'ticket.delivery_method',
                fieldKey: 'TICKET.EXPORT.OPERATION_DATA.DELIVERY',
                isDefault: false
            },
            {
                field: 'ticket.insurance.name',
                fieldKey: 'TICKET.EXPORT.OPERATION_DATA.INSURANCE_NAME',
                isDefault: false
            },
            {
                field: 'ticket.insurance.insurer.name',
                fieldKey: 'TICKET.EXPORT.OPERATION_DATA.INSURER_NAME',
                isDefault: false
            },
            {
                field: 'ticket.insurance.policy.name',
                fieldKey: 'TICKET.EXPORT.OPERATION_DATA.POLICY_NAME',
                isDefault: false
            },
            {
                field: 'ticket.client.client_b2b.client_category',
                fieldKey: 'TICKET.EXPORT.OPERATION_DATA.B2B_CLIENT_CATEGORY',
                isDefault: true
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
                field: 'ticket.sales.automatic.name',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.SALES_AUTOMATIC_NAME',
                isDefault: true
            },
            {
                field: 'price.sales.automatic',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.SALES_AUTOMATIC',
                isDefault: true
            },
            {
                field: 'ticket.sales.discount.name',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.SALES_DISCOUNT_NAME',
                isDefault: true
            },
            {
                field: 'ticket.sales.discount.activator.code',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.SALES_DISCOUNT_ACTIVATOR_CODE',
                isDefault: false
            },
            {
                field: 'price.sales.discount',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.SALES_DISCOUNT',
                isDefault: true
            },
            {
                field: 'ticket.sales.discount.activator.collective.name',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.SALES_DISCOUNT_ACTIVATOR_COLLECTIVE_NAME',
                isDefault: false
            },
            {
                field: 'ticket.sales.promotion.name',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.SALES_PROMOTION_NAME',
                isDefault: true
            },
            {
                field: 'ticket.sales.promotion.activator.code',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.SALES_PROMOTION_ACTIVATOR_CODE',
                isDefault: false
            },
            {
                field: 'price.sales.promotion',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.SALES_PROMOTION',
                isDefault: true
            },
            {
                field: 'ticket.sales.promotion.activator.collective.name',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.SALES_PROMOTION_ACTIVATOR_COLLECTIVE_NAME',
                isDefault: false
            },
            {
                field: 'ticket.sales.order_automatic',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.SALES_ORDER_AUTOMATIC',
                isDefault: true
            },
            {
                field: 'ticket.sales.order_automatic.name',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.SALES_ORDER_AUTOMATIC_NAME',
                isDefault: true
            },
            {
                field: 'ticket.sales.order_collective',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.SALES_ORDER_COLLECTIVE',
                isDefault: true
            },
            {
                field: 'ticket.sales.order_collective.name',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.SALES_ORDER_COLLECTIVE_NAME',
                isDefault: true
            },
            {
                field: 'ticket.sales.order_collective.activator.collective.name',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.SALES_ORDER_COLLECTIVE_ACTIVATOR_COLLECTIVE_NAME',
                isDefault: false
            },
            {
                field: 'ticket.sales.order_collective.activator.code',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.SALES_ORDER_COLLECTIVE_ACTIVATOR_CODE',
                isDefault: false
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
                field: 'price.item',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.PRICE_ITEM',
                isDefault: true
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
                field: 'price.charges.net.channel',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.CHARGES_NET_CHANNEL',
                isDefault: false
            },
            {
                field: 'price.taxes.charges.channel.value',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.CHARGES_CHANNEL_TAX_RATE',
                isDefault: false
            },
            {
                field: 'price.taxes.charges.channel',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.TAXES_CHARGES_CHANNEL',
                isDefault: false
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
                field: 'price.delivery',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.DELIVERY',
                isDefault: false
            },
            {
                field: 'price.insurance',
                fieldKey: 'TICKET.EXPORT.PAYMENT_DATA.INSURANCE',
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

