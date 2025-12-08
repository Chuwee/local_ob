import { ListResponse } from '@OneboxTM/utils-state';
import { SessionType } from '@admin-clients/cpanel/promoters/events/sessions/data-access';

export interface SaleRequestSessionResponse extends ListResponse<SaleRequestSession> {
}

export interface SaleRequestSession {
    id: number;
    name: string;
    type: SessionType;
    status: string;
    date: {
        start: string;
        end: string;
        publication: string;
        sales_start: string;
        sales_end: string;
        booking_start: string;
        booking_end: string;
    };
}
