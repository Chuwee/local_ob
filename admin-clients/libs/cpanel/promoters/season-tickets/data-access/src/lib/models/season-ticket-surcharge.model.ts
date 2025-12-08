import { RangeElement } from '@admin-clients/shared-utility-models';

export enum SeasonTicketSurchargeType {
    generic = 'GENERIC',
    promotion = 'PROMOTION',
    invitation = 'INVITATION',
    changeSeat = 'CHANGE_SEAT',
    secondaryMarket = 'SECONDARY_MARKET_PROMOTER'
}

export interface SeasonTicketSurcharge {
    type: SeasonTicketSurchargeType;
    limit: {
        enabled: boolean;
        min?: number;
        max?: number;
    };
    ranges: RangeElement[];
}
