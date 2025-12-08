export interface ChannelLoyaltyPoints {
    allow_loyalty_points: boolean;
    max_loyalty_points_per_purchase?: {
        enabled: boolean;
        amount?: number;
    };
    loyalty_points_percentage_per_purchase?: {
        enabled: boolean;
        percentage?: number;
    };
}
