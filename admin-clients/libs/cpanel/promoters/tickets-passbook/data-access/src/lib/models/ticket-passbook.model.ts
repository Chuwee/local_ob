import { TicketPassbookDesign } from './ticket-passbook-design.enum';
import { TicketPassbookType } from './ticket-passbook-type.enum';

export interface TicketPassbook {
    code: string;
    entity_id: number;
    operator_id: number;
    type: TicketPassbookType;
    name: string;
    obfuscated_barcode: boolean;
    default_passbook: boolean;
    description: string;
    passbook_design: TicketPassbookDesign;
    languages: {
        default: string;
        selected: string[];
    };
    create_date: string;
    update_date: string;
    header: TicketPassbookFields;
    primary_field: TicketPassbookFields;
    secondary_fields: TicketPassbookFields[];
    auxiliary_fields: TicketPassbookFields[];
    back_fields: TicketPassbookFields[];
}

export interface PutTicketPassbook {
    code: string;
    entity_id?: number;
    operator_id?: number;
    name?: string;
    obfuscated_barcode?: boolean;
    default_passbook?: boolean;
    description?: string;
    passbook_design?: TicketPassbookDesign;
    languages?: {
        default: string;
        selected: string[];
    };
    create_date?: string;
    update_date?: string;
    header?: TicketPassbookFields;
    primary_field?: TicketPassbookFields;
    secondary_fields?: TicketPassbookFields[];
    auxiliary_fields?: TicketPassbookFields[];
    back_fields?: TicketPassbookFields[];
}

export interface TicketPassbookFields {
    key: string;
    label: string[];
    value: string[];
    group?: string;
}
