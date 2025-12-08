import { SubscriptionListStatus } from './subscription-list-status.enum';

export interface SubscriptionListFilter {
    entityId?: number;
    status?: SubscriptionListStatus;
    q?: string;
}
