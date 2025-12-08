import { FieldDataGroup } from '@admin-clients/shared/data-access/models';

export const exportDataTicketAction: FieldDataGroup[] = [
    {
        fieldKey: 'TICKET.EXPORT.TICKET_DATA.TITLE',
        field: 'ticket_data',
        isDefault: false,
        fields: [
            {
                field: 'ticket.allocation.event.name',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.EVENT_NAME',
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
                field: 'order.code',
                fieldKey: 'TICKET.EXPORT.OPERATION_DATA.ORDER_CODE',
                isDefault: true
            },
            {
                field: 'ticket.barcode.code',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.BARCODE',
                isDefault: true
            },
            {
                field: 'ticket.total_prints',
                fieldKey: 'TICKET.EXPORT.TICKET_DATA.PRINTS',
                isDefault: true
            }
        ]
    },
    {
        fieldKey: 'TICKET.EXPORT.TICKET_ACTIONS.TITLE',
        field: 'ticket_data',
        isDefault: false,
        fields: [
            {
                field: 'actions_history.date',
                fieldKey: 'TICKET.EXPORT.TICKET_ACTION.DATE',
                isDefault: true
            },
            {
                field: 'actions_history.user.username',
                fieldKey: 'TICKET.EXPORT.TICKET_ACTION.USERNAME',
                isDefault: true
            },
            {
                field: 'actions_history.ticket_format',
                fieldKey: 'TICKET.EXPORT.TICKET_ACTION.TICKET_FORMAT',
                isDefault: true
            },
            {
                field: 'actions_history.type',
                fieldKey: 'TICKET.EXPORT.TICKET_ACTION.TYPE',
                isDefault: true
            }
        ]
    }
];
