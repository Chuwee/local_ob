export interface EntityLoyaltyPoints {
    point_exchange: {
        code: string;
        value: number;
    }[];
    expiration?: {
        enabled: boolean;
        months: number;
    };
    max_points?: {
        enabled: boolean;
        amount: number;
    };
    last_reset?: string;
}