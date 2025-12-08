import { SubscriptionListStatus } from './subscription-list-status.enum';

export interface PutSubscriptionList {
    name: string;
    description: string;
    status: SubscriptionListStatus;
    default: boolean;
}
