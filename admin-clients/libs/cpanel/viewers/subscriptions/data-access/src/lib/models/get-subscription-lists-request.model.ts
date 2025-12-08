import { SubscriptionListStatus } from './subscription-list-status.enum';

export interface GetSubscriptionListsRequest {
    entityId?: number;
    status?: SubscriptionListStatus;
    q?: string;
}
