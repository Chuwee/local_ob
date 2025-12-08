export interface SubscriptionModeCommunication {
    name: string;
    description: string;
    link?: string;
    link_text?: string;
}

export type SubscriptionModeCommunications = Record<string, SubscriptionModeCommunication>;

export interface SubscriptionMode {
    sid: string;
    name: string;
    active: boolean;
    capacities: number[];
    periodicities: number[];
    roles: number[];
    default_buy_periodicity: number;
    default_buy_role_id: number;
}
