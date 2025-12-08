import { PromotionSessionsScope } from '@admin-clients/cpanel/promoters/data-access';

export interface PutEventPromotionSessions {
    type: PromotionSessionsScope;
    sessions: number[];
}
