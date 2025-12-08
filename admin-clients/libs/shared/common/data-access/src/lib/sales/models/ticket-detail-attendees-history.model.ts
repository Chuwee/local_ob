import { IdName } from '@admin-clients/shared/data-access/models';

export interface TicketAttendeeHistory {
    fields: Map<string, string>;
    action: {
        invalidation_date: string;
        user: {
            id: number;
            name: string;
            username: string;
        };
        channel: IdName[];
    };
}
