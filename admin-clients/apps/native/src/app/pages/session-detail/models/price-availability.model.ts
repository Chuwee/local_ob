import { ActivityLimitType } from '@admin-clients/shared/venues/data-access/activity-venue-tpls';

export type PriceAvailabilityModel = {
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
