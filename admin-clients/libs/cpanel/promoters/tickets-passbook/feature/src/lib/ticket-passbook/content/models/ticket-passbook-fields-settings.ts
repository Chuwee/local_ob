import { TicketPassbookFieldsGroup } from './ticket-passbook-fields-group.enum';

export const ticketPassbookFieldsSettings = new Map<TicketPassbookFieldsGroup, { headerTitle: string; maxContent?: number }>([
    [TicketPassbookFieldsGroup.header, {
        headerTitle: 'SECTION_HEADER_CONTENT',
        maxContent: 1
    }],
    [TicketPassbookFieldsGroup.primaryField, {
        headerTitle: 'SECTION_PRINCIPAL_CONTENT',
        maxContent: 1
    }],
    [TicketPassbookFieldsGroup.secondaryFields, {
        headerTitle: 'SECTION_SECONDARY_CONTENT',
        maxContent: 4
    }],
    [TicketPassbookFieldsGroup.auxiliaryFields, {
        headerTitle: 'SECTION_AUXILIARY_CONTENT',
        maxContent: 4
    }],
    [TicketPassbookFieldsGroup.backFields, {
        headerTitle: 'SECTION_BACK_CONTENT'
    }]
]);
