import { TicketPrintType } from './ticket-print-type.enum';

export interface TicketPrint {
    date: string;
    user?: {
        id: number;
        username: string;
    };
    entity?: {
        id: number;
        name: string;
    };
    type?: TicketPrintType;
}
