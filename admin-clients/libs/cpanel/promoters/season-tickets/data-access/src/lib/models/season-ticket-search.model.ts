import { Category } from '@admin-clients/shared/common/data-access';
import { SeasonTicketGenerationStatus, SeasonTicketStatus } from './season-ticket-status.model';

export interface SeasonTicketSearch {
    id: number;
    name: string;
    reference: string;
    category: Category;
    entity: {
        id: number;
        name: string;
    };
    producer: {
        id: number;
        name: string;
    };
    status: SeasonTicketStatus;
    session_id: number;
    start_date: string;
    end_date: string;
    currency_code: string;
    venue_templates: {
        id: number;
        name: string;
        venue: {
            name: string;
        };
    }[];
    allow_renewal: boolean;
    allow_change_seat: boolean;
    generation_status: SeasonTicketGenerationStatus;
}
