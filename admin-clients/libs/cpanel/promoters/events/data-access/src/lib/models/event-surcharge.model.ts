import { RangeElement } from '@admin-clients/shared-utility-models';

export enum EventSurchargeType {
    generic = 'GENERIC',
    promotion = 'PROMOTION',
    invitation = 'INVITATION',
    secondaryMarket = 'SECONDARY_MARKET_PROMOTER',
    changeSeat = 'CHANGE_SEAT'
}
export interface EventSurcharge {
    type: EventSurchargeType;
    limit?: {
        enabled: boolean;
        min?: number;
        max?: number;
    };
    ranges: RangeElement[];
    allow_channel_use_alternative_charges?: boolean;
}
