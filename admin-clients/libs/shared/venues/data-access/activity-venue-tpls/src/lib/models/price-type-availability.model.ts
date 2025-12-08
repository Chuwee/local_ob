import { IdName } from '@admin-clients/shared/data-access/models';
import { ActivityTicketType } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ActivityLimitType } from './activity-limit-type.enum';

export interface PriceTypeAvailability {
    price_type: IdName;
    quota: IdName;
    ticket_type?: ActivityTicketType;
    availability: {
        total: {
            type: ActivityLimitType;
            value: number;
        };
        available: number;
        promoter_blocked: number;
        kill: number;
        purchase: number;
        invitation: number;
        booking: number;
        issue: number;
        in_progress: number;
        session_pack: number;
    };
}
