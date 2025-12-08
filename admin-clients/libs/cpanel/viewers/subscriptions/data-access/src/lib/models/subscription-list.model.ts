import { SubscriptionListStatus } from './subscription-list-status.enum';

export interface SubscriptionList {
    id: number;
    name: string;
    description: string;
    uses: number;
    default: boolean;
    status: SubscriptionListStatus;
    entity: {
        id: number;
        name: string;
    };
}
