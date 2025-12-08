import { TicketsFields } from '@admin-clients/cpanel-sales-data-access';
import { FieldDataGroup } from '@admin-clients/shared/data-access/models';

export const ticketsColumnList: FieldDataGroup[] = [
    {
        field: 'tickets',
        fieldKey: 'TICKETS',
        isDefault: false,
        fields: [
            {
                field: TicketsFields.code,
                fieldKey: 'FORMS.LABELS.CODE',
                isDefault: true,
                disabled: true
            },
            {
                field: TicketsFields.event,
                fieldKey: 'TICKET.EVENT_NAME',
                isDefault: true
            },
            {
                field: TicketsFields.session,
                fieldKey: 'TICKET.SESSION_NAME',
                isDefault: true
            },
            {
                field: TicketsFields.sessionDate,
                fieldKey: 'TICKET.SESSION_DATE',
                isDefault: true
            },
            {
                field: TicketsFields.sector,
                fieldKey: 'TICKET.SECTOR',
                isDefault: true
            },
            {
                field: TicketsFields.priceType,
                fieldKey: 'TICKET.PRICE_TYPE',
                isDefault: true
            },
            {
                field: TicketsFields.barcode,
                fieldKey: 'TICKET.BARCODE',
                isDefault: true
            },
            {
                field: TicketsFields.purchaseDate,
                fieldKey: 'TICKET.PURCHASE_DATE',
                isDefault: true
            },
            {
                field: TicketsFields.channel,
                fieldKey: 'TICKET.CHANNEL_NAME',
                isDefault: true
            },
            {
                field: TicketsFields.client,
                fieldKey: 'TICKET.CLIENT',
                isDefault: true
            },
            {
                field: TicketsFields.prints,
                fieldKey: 'TICKET.PRINTS',
                isDefault: true
            },
            {
                field: TicketsFields.validation,
                fieldKey: 'TICKET.VALIDATION',
                isDefault: true
            },
            {
                field: TicketsFields.state,
                fieldKey: 'TICKET.STATE',
                isDefault: true
            },
            {
                field: TicketsFields.originMarket,
                fieldKey: 'TICKET.ORIGIN_MARKET',
                isDefault: false
            },
            {
                field: TicketsFields.price,
                fieldKey: 'TICKET.FINAL_PRICE',
                isDefault: true,
                disabled: true
            }
        ]
    }
];
