import { ActionsHistoryType } from './actions-history-type.enum';
import { TicketPrintType } from './ticket-print-type.enum';

export interface ActionsHistory {
    date: string;
    type: ActionsHistoryType;
    ticket_format: TicketPrintType;
    user: {
        id: number;
        username: string;
        name: string;
    };
    channel?: {
        id: number;
        name: string;
    };
    additional_data?: {
        resend_email?: string;
        resend_whatsapp?: string;
        external_center_code?: string;
        external_company_code?: string;
        external_user_id?: string;
        external_username?: string;
    };
}
