import { FieldDataGroup } from '@admin-clients/shared/data-access/models';

export const exportDataOrder: FieldDataGroup[] = [
    {
        fieldKey: 'ORDER.EXPORT.OPERATION_DATA.TITLE',
        field: 'operation_data',
        isDefault: true,
        fields: [
            {
                field: 'type',
                fieldKey: 'ORDER.EXPORT.OPERATION_DATA.TYPE',
                isDefault: true
            },
            {
                field: 'code',
                fieldKey: 'ORDER.EXPORT.OPERATION_DATA.CODE',
                isDefault: true
            },
            {
                field: 'date.date',
                fieldKey: 'ORDER.EXPORT.OPERATION_DATA.DATE',
                isDefault: true
            },
            {
                field: 'date.time',
                fieldKey: 'ORDER.EXPORT.OPERATION_DATA.TIME',
                isDefault: true
            },
            {
                field: 'booking_expires',
                fieldKey: 'ORDER.EXPORT.OPERATION_DATA.BOOKING_EXPIRES',
                isDefault: false
            },
            {
                field: 'channel.entity.name',
                fieldKey: 'ORDER.EXPORT.OPERATION_DATA.CHANNEL_ENTITY_NAME',
                isDefault: true
            },
            {
                field: 'channel.name',
                fieldKey: 'ORDER.EXPORT.OPERATION_DATA.CHANNEL_NAME',
                isDefault: true
            },
            {
                field: 'point_of_sale.name',
                fieldKey: 'ORDER.EXPORT.OPERATION_DATA.POINT_OF_SALE',
                isDefault: true
            },
            {
                field: 'terminal.name',
                fieldKey: 'ORDER.EXPORT.OPERATION_DATA.TERMINAL',
                isDefault: true
            },
            {
                field: 'user.username',
                fieldKey: 'ORDER.EXPORT.OPERATION_DATA.USER_NAME',
                isDefault: true
            },
            {
                field: 'items.previous_order.type',
                fieldKey: 'ORDER.EXPORT.OPERATION_DATA.PREVIOUS_ORDER_TYPE',
                isDefault: true
            },
            {
                field: 'items.previous_order.code',
                fieldKey: 'ORDER.EXPORT.OPERATION_DATA.PREVIOUS_ORDER_CODE',
                isDefault: true
            },
            {
                field: 'items.previous_order.date.date',
                fieldKey: 'TICKET.EXPORT.OPERATION_DATA.PREVIOUS_ORDER_DATE',
                isDefault: true
            },
            {
                field: 'items.previous_order.date.time',
                fieldKey: 'TICKET.EXPORT.OPERATION_DATA.PREVIOUS_ORDER_DATE_TIME',
                isDefault: true
            },
            {
                field: 'items_count',
                fieldKey: 'ORDER.EXPORT.OPERATION_DATA.ITEMS_COUNT',
                isDefault: true
            },
            {
                field: 'general_tickets_count',
                fieldKey: 'ORDER.EXPORT.OPERATION_DATA.GENERAL_ITEMS_COUNT',
                isDefault: true
            },
            {
                field: 'products_count',
                fieldKey: 'ORDER.EXPORT.OPERATION_DATA.PRODUCTS_COUNT',
                isDefault: true
            },
            {
                field: 'invitation_tickets_count',
                fieldKey: 'ORDER.EXPORT.OPERATION_DATA.INVITATION_ITEMS_COUNT',
                isDefault: true
            },
            {
                field: 'primary_market_tickets_count',
                fieldKey: 'ORDER.EXPORT.OPERATION_DATA.PRIMARY_MARKET_ITEMS_COUNT',
                isDefault: false
            },
            {
                field: 'secondary_market_tickets_count',
                fieldKey: 'ORDER.EXPORT.OPERATION_DATA.SECONDARY_MARKET_ITEMS_COUNT',
                isDefault: false
            },
            {
                field: 'delivery',
                fieldKey: 'ORDER.EXPORT.OPERATION_DATA.DELIVERY',
                isDefault: true
            },
            {
                field: 'price.gateway',
                fieldKey: 'ORDER.EXPORT.OPERATION_DATA.GATEWAY_PRICE',
                isDefault: false
            },
            {
                field: 'insurance.name',
                fieldKey: 'ORDER.EXPORT.OPERATION_DATA.INSURANCE_NAME',
                isDefault: true
            },
            {
                field: 'insurance.insurer_name',
                fieldKey: 'ORDER.EXPORT.OPERATION_DATA.INSURER_NAME',
                isDefault: true
            },
            {
                field: 'notes',
                fieldKey: 'ORDER.EXPORT.OPERATION_DATA.NOTES',
                isDefault: true
            },
            {
                field: 'buyer_data.channel_agreements',
                fieldKey: 'ORDER.EXPORT.OPERATION_DATA.CHANNEL_AGREEMENTS',
                isDefault: true
            },
            {
                field: 'external_code',
                fieldKey: 'ORDER.EXPORT.OPERATION_DATA.EXTERNAL_CODE',
                isDefault: true
            },
            {
                field: 'invoice.number',
                fieldKey: 'ORDER.EXPORT.OPERATION_DATA.INVOICE_NUMBER',
                isDefault: true
            }
        ]
    }, {
        fieldKey: 'ORDER.EXPORT.REALLOCATION_DATA.TITLE',
        field: 'reallocation_data',
        isDefault: false,
        fields: [
            {
                field: 'price.reallocation_refund',
                fieldKey: 'ORDER.EXPORT.REALLOCATION_DATA.REALLOCATION_REFUND',
                isDefault: false
            },
            {
                field: 'price.charges.reallocation',
                fieldKey: 'ORDER.EXPORT.REALLOCATION_DATA.CHARGES.REALLOCATION',
                isDefault: false
            }
        ]
    }, {
        fieldKey: 'ORDER.EXPORT.CHANGE_SEAT_DATA.TITLE',
        field: 'change_seat_data',
        isDefault: false,
        fields: [
            {
                field: 'reallocation_refund',
                fieldKey: 'ORDER.EXPORT.CHANGE_SEAT_DATA.REALLOCATION_REFUND',
                isDefault: false
            },
            {
                field: 'price.charges.net.ticket_reallocation',
                fieldKey: 'ORDER.EXPORT.CHANGE_SEAT_DATA.NET_SURCHARGE',
                isDefault: false
            },
            {
                field: 'price.taxes.charges.ticket_reallocation',
                fieldKey: 'ORDER.EXPORT.CHANGE_SEAT_DATA.TAX_SURCHARGE',
                isDefault: false
            },
            {
                field: 'price.charges.ticket_reallocation',
                fieldKey: 'ORDER.EXPORT.CHANGE_SEAT_DATA.CHARGES.REALLOCATION',
                isDefault: false
            }
        ]
    }, {
        fieldKey: 'ORDER.EXPORT.BUYER_DATA.TITLE',
        field: 'buyer_data',
        isDefault: false,
        fields: [
            {
                field: 'client_type',
                fieldKey: 'ORDER.EXPORT.BUYER_DATA.CLIENT_TYPE',
                isDefault: true
            },
            {
                field: 'buyer_data.name',
                fieldKey: 'ORDER.EXPORT.BUYER_DATA.NAME',
                isDefault: true
            },
            {
                field: 'buyer_data.surname',
                fieldKey: 'ORDER.EXPORT.BUYER_DATA.SURNAME',
                isDefault: true
            },
            {
                field: 'buyer_data.email',
                fieldKey: 'ORDER.EXPORT.BUYER_DATA.EMAIL',
                isDefault: true
            },
            {
                field: 'buyer_data.phone',
                fieldKey: 'ORDER.EXPORT.BUYER_DATA.PHONE',
                isDefault: true
            },
            {
                field: 'buyer_data.international_phone.prefix',
                fieldKey: 'ORDER.EXPORT.BUYER_DATA.INTERNATIONAL_PHONE.PREFIX',
                isDefault: false
            },
            {
                field: 'buyer_data.international_phone.number',
                fieldKey: 'ORDER.EXPORT.BUYER_DATA.INTERNATIONAL_PHONE.NUMBER',
                isDefault: false
            },
            {
                field: 'buyer_data.country',
                fieldKey: 'ORDER.EXPORT.BUYER_DATA.COUNTRY',
                isDefault: true
            },
            {
                field: 'buyer_data.country_subdivision',
                fieldKey: 'ORDER.EXPORT.BUYER_DATA.COUNTRY_SUBDIVISION',
                isDefault: true
            },
            {
                field: 'buyer_data.zip_code',
                fieldKey: 'ORDER.EXPORT.BUYER_DATA.ZIP_CODE',
                isDefault: true
            },
            {
                field: 'buyer_data.address',
                fieldKey: 'ORDER.EXPORT.BUYER_DATA.ADDRESS',
                isDefault: true
            },
            {
                field: 'buyer_data.id_number',
                fieldKey: 'ORDER.EXPORT.BUYER_DATA.ID_NUMBER',
                isDefault: true
            },
            {
                field: 'buyer_data.allow_commercial_mailing',
                fieldKey: 'ORDER.EXPORT.BUYER_DATA.ALLOW_COMMERCIAL_MAILING',
                isDefault: false
            },
            {
                field: 'buyer_data.profile_data.name',
                fieldKey: 'ORDER.EXPORT.BUYER_DATA.PROFILE_DATA_KEY',
                isDefault: true
            },
            {
                field: 'buyer_data.profile_data.attributes',
                fieldKey: 'ORDER.EXPORT.BUYER_DATA.PROFILE_DATA_ATTRIBUTES',
                isDefault: true
            }
        ]
    },
    {
        fieldKey: 'ORDER.EXPORT.INVOICE_DATA.TITLE',
        field: 'buyer_data.invoice',
        isDefault: false,
        fields: [
            {
                field: 'buyer_data.invoice.name',
                fieldKey: 'ORDER.EXPORT.INVOICE_DATA.NAME',
                isDefault: false
            },
            {
                field: 'buyer_data.invoice.address',
                fieldKey: 'ORDER.EXPORT.INVOICE_DATA.ADDRESS',
                isDefault: false
            },
            {
                field: 'buyer_data.invoice.zip_code',
                fieldKey: 'ORDER.EXPORT.INVOICE_DATA.ZIP_CODE',
                isDefault: false
            },
            {
                field: 'buyer_data.invoice.country',
                fieldKey: 'ORDER.EXPORT.INVOICE_DATA.COUNTRY',
                isDefault: false
            },
            {
                field: 'buyer_data.invoice.country_subdivision',
                fieldKey: 'ORDER.EXPORT.INVOICE_DATA.COUNTRY_SUBDIVISION',
                isDefault: false
            },
            {
                field: 'buyer_data.invoice.city',
                fieldKey: 'ORDER.EXPORT.INVOICE_DATA.CITY',
                isDefault: false
            },
            {
                field: 'buyer_data.invoice.id_number_type',
                fieldKey: 'ORDER.EXPORT.INVOICE_DATA.ID_NUMBER_TYPE',
                isDefault: false
            },
            {
                field: 'buyer_data.invoice.id_number',
                fieldKey: 'ORDER.EXPORT.INVOICE_DATA.ID_NUMBER',
                isDefault: false
            }
        ]
    },
    {
        fieldKey: 'ORDER.EXPORT.DONATIONS_DATA.TITLE',
        field: 'donation_data',
        isDefault: false,
        fields: [
            {
                field: 'donation.id',
                fieldKey: 'ORDER.EXPORT.DONATION_DATA.DONATION.ID',
                isDefault: false
            },
            {
                field: 'donation.provider',
                fieldKey: 'ORDER.EXPORT.DONATION_DATA.DONATION.PROVIDER',
                isDefault: false
            },
            {
                field: 'donation.campaign',
                fieldKey: 'ORDER.EXPORT.DONATION_DATA.DONATION.CAMPAIGN',
                isDefault: false
            },
            {
                field: 'price.donation',
                fieldKey: 'ORDER.EXPORT.DONATION_DATA.PRICE.DONATION',
                isDefault: false
            }
        ]
    },
    {
        fieldKey: 'ORDER.EXPORT.B2B_DATA.TITLE',
        field: 'b2b_data',
        isDefault: false,
        fields: [
            {
                field: 'buyer_data.b2b_data.name',
                fieldKey: 'ORDER.EXPORT.B2B_DATA.B2B_NAME',
                isDefault: false
            }
        ]
    },
    {
        fieldKey: 'ORDER.EXPORT.TICKET_DATA.TITLE',
        field: 'ticket_data',
        isDefault: false,
        fields: [
            {
                field: 'items.ticket.allocation.event.name',
                fieldKey: 'ORDER.EXPORT.TICKET_DATA.EVENT',
                isDefault: true
            },
            {
                field: 'items.ticket.allocation.session.name',
                fieldKey: 'ORDER.EXPORT.TICKET_DATA.SESSION',
                isDefault: true
            },
            {
                field: 'items.ticket.allocation.session.date.start',
                fieldKey: 'ORDER.EXPORT.TICKET_DATA.SESSION_DATE',
                isDefault: true
            },
            {
                field: 'items.ticket.allocation.venue.name',
                fieldKey: 'ORDER.EXPORT.TICKET_DATA.VENUE',
                isDefault: true
            },
            {
                field: 'items.ticket.allocation.event.entity.name',
                fieldKey: 'ORDER.EXPORT.TICKET_DATA.EVENT_ENTITY',
                isDefault: true
            },
            {
                field: 'items_multiple_sessions',
                fieldKey: 'ORDER.EXPORT.TICKET_DATA.MULTIPLE',
                isDefault: false
            }
        ]
    },
    {
        fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.TITLE',
        field: 'payment_data',
        isDefault: false,
        fields: [
            {
                field: 'price.currency',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.CURRENCY',
                isDefault: true
            },
            {
                field: 'price.net',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.NET_ITEMS',
                isDefault: false
            },
            {
                field: 'price.base',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.BASE_PRICE',
                isDefault: true
            },
            {
                field: 'price.sales.automatic',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.SALES_AUTOMATIC',
                isDefault: true
            },
            {
                field: 'price.sales.discount',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.SALES_DISCOUNT',
                isDefault: true
            },
            {
                field: 'price.sales.promotion',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.SALES_PROMOTION',
                isDefault: true
            },
            {
                field: 'price.sales.order_automatic',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.SALES_ORDER_AUTOMATIC',
                isDefault: true
            },
            {
                field: 'price.sales.order_automatic.name',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.SALES_ORDER_AUTOMATIC_NAME',
                isDefault: true
            },
            {
                field: 'price.sales.order_collective',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.SALES_ORDER_COLLECTIVE',
                isDefault: true
            },
            {
                field: 'price.sales.order_collective.name',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.SALES_ORDER_COLLECTIVE_NAME',
                isDefault: true
            },
            {
                field: 'price.sales.order_collective.activator.collective.name',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.SALES_ORDER_COLLECTIVE_ACTIVATOR_COLLECTIVE_NAME',
                isDefault: false
            },
            {
                field: 'price.sales.order_collective.activator.code',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.SALES_ORDER_COLLECTIVE_ACTIVATOR_CODE',
                isDefault: false
            },
            {
                field: 'price.b2b.conditions',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.B2B_CONDITIONS',
                isDefault: true
            },
            {
                field: 'price.pre_tax',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.PRE_TAX_ITEMS',
                isDefault: false
            },
            {
                field: 'price.taxes.items',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.TAXES_ITEMS',
                isDefault: false
            },
            {
                field: 'price.selling',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.SELLING_PRICE',
                isDefault: true
            },
            {
                field: 'price.charges.net.promoter',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.CHARGES_NET_PROMOTER',
                isDefault: false
            },
            {
                field: 'price.taxes.charges.promoter',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.TAXES_CHARGES_PROMOTER',
                isDefault: false
            },
            {
                field: 'price.charges.promoter',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.CHARGES_PROMOTER',
                isDefault: true
            },
            {
                field: 'price.taxes.charges.channel',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.TAXES_CHARGES_CHANNEL',
                isDefault: false
            },
            {
                field: 'price.charges.net.channel',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.CHARGES_NET_CHANNEL',
                isDefault: false
            },
            {
                field: 'price.charges.channel',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.CHARGES_CHANNEL',
                isDefault: true
            },
            {
                field: 'price.charges.secondary_market_promoter',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.SECONDARY_MARKET_CHARGES_PROMOTER',
                isDefault: false
            },
            {
                field: 'price.charges.secondary_market_channel',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.SECONDARY_MARKET_CHARGES_CHANNEL',
                isDefault: false
            },
            {
                field: 'price.delivery_breakdown.net',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.DELIVERY_BREAKDOWN.NET',
                isDefault: false
            },
            {
                field: 'price.delivery_breakdown.taxes.value',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.DELIVERY_BREAKDOWN.TAXES_VALUE',
                isDefault: false
            },
            {
                field: 'price.delivery_breakdown.taxes.total',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.DELIVERY_BREAKDOWN.TAXES_TOTAL',
                isDefault: false
            },
            {
                field: 'price.delivery',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.DELIVERY',
                isDefault: true
            },
            {
                field: 'price.insurance_breakdown.net',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.INSURANCE_BREAKDOWN.NET',
                isDefault: false
            },
            {
                field: 'price.insurance_breakdown.taxes.value',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.INSURANCE_BREAKDOWN.TAXES_VALUE',
                isDefault: false
            },
            {
                field: 'price.insurance_breakdown.taxes.total',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.INSURANCE_BREAKDOWN.TAXES_TOTAL',
                isDefault: false
            },
            {
                field: 'price.insurance',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.INSURANCE',
                isDefault: true
            },
            {
                field: 'price.gateway_breakdown.net',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.GATEWAY_BREAKDOWN.NET',
                isDefault: false
            },
            {
                field: 'price.gateway_breakdown.taxes.value',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.GATEWAY_BREAKDOWN.TAXES_VALUE',
                isDefault: false
            },
            {
                field: 'price.gateway_breakdown.taxes.total',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.GATEWAY_BREAKDOWN.TAXES_TOTAL',
                isDefault: false
            },
            {
                field: 'price.gateway',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.GATEWAY_BREAKDOWN.TOTAL',
                isDefault: false
            },
            {
                field: 'price.final',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.FINAL_PRICE',
                isDefault: true
            },
            {
                field: 'payment_data.value.cash',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.VALUE_CASH',
                isDefault: true
            },
            {
                field: 'payment_data.value.credit_card',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.VALUE_CREDIT_CARD',
                isDefault: true
            },
            {
                field: 'payment_data.value.bank_transfer',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.VALUE_BANK_TRANSFER',
                isDefault: true
            },
            {
                field: 'payment_data.value.client_balance',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.VALUE_CLIENT_BALANCE',
                isDefault: true
            },
            {
                field: 'payment_data.value.voucher',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.VALUE_VOUCHER',
                isDefault: true
            },
            {
                field: 'payment_data.value.bizum',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.VALUE_BIZUM',
                isDefault: true
            },
            {
                field: 'payment_data.value.offline',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.VALUE_OFFLINE',
                isDefault: true
            },
            {
                field: 'payment_data.value.other',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.VALUE_OTHER',
                isDefault: false
            },
            {
                field: 'payment_data.value.external',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.VALUE_EXTERNAL',
                isDefault: false
            },
            {
                field: 'payment_data.merchant',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.MERCHANT',
                isDefault: true
            },
            {
                field: 'payment_detail.authorization_code',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.AUTHORIZATION_CODE',
                isDefault: true
            },
            {
                field: 'payment_detail.gateway',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.GATEWAY',
                isDefault: true
            },
            {
                field: 'payment_data.reference',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.REFERENCE',
                isDefault: true
            },
            {
                field: 'price.channel_commission',
                fieldKey: 'ORDER.EXPORT.PAYMENT_DATA.CHANNEL_COMMISSIONS',
                isDefault: true
            }
        ]
    },
    {
        fieldKey: 'ORDER.EXPORT.CUSTOMER.TITLE',
        field: 'customer',
        isDefault: false,
        fields: [
            {
                field: 'customer.metadata',
                fieldKey: 'ORDER.EXPORT.CUSTOMER.METADATA',
                isDefault: false
            }
        ]
    },
    {
        fieldKey: 'ORDER.EXPORT.CONSUMER.TITLE',
        field: 'consumer',
        isDefault: false,
        fields: [
            {
                field: 'consumer.metadata',
                fieldKey: 'ORDER.EXPORT.CONSUMER.METADATA',
                isDefault: false
            }
        ]
    }
];

export const exportLoyaltyProgramOrderData: FieldDataGroup = {
    fieldKey: 'ORDER.EXPORT.LOYALTY_PROGRAM_DATA.TITLE',
    field: 'loyalty_program_data',
    isDefault: false,
    fields: [
        {
            field: 'loyalty_program.redeemed',
            fieldKey: 'ORDER.EXPORT.LOYALTY_PROGRAM_DATA.REDEEMED',
            isDefault: false
        },
        {
            field: 'loyalty_program.earned',
            fieldKey: 'ORDER.EXPORT.LOYALTY_PROGRAM_DATA.EARNED',
            isDefault: false
        }
    ]
};

