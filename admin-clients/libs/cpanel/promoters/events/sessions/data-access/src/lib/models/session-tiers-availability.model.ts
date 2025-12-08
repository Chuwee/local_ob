export interface SessionTiersAvailability {
    price_type: {
        id: number;
        name: string;
        capacity: number;
    };
    quotas: {
        id: number;
        name: string;
        limit: number;
    };
    tier: {
        id: number;
        name: string;
        limit: number;
        active: boolean;
    };
    sold: number;
    refunded: number;
}
