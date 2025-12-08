import { PromotionSessionsScope } from '@admin-clients/cpanel/promoters/data-access';
import { SessionType } from '@admin-clients/cpanel/promoters/events/sessions/data-access';

export interface EventPromotionSessions {
    type: PromotionSessionsScope;
    sessions: {
        id: number;
        name: string;
        date?: string;
        type?: SessionType;
    }[];
}
