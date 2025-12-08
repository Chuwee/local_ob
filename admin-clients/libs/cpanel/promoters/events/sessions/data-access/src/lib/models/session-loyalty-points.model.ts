import { SessionLoyaltyPointsGainType } from './session-loyalty-points-gain-type.enum';

export interface SessionLoyaltyPoints {
    point_gain?: {
        amount: number;
        type: SessionLoyaltyPointsGainType;
    };
}
